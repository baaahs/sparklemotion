package baaahs.plugin.beatlink

import baaahs.ui.Observable
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import baaahs.util.asDoubleSeconds
import kotlinx.datetime.Instant
import org.deepsymmetry.beatlink.*
import org.deepsymmetry.beatlink.data.*
import org.jetbrains.annotations.VisibleForTesting
import kotlin.concurrent.thread
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

/**
 * Listens to all connected CDJs' beat and tempo updates.
 *
 * We pick the CDJ to sync shows to based on the following priority:
 * 1) The CDJ that is currently on-air (has its fader up on the mixer)
 * 2) If more than one CDJ is on-air, pick the CDJ that is the Tempo Master
 */
class BeatLinkBeatSource(
    private val clock: Clock,
    private val timeFinder: TimeFinder = TimeFinder.getInstance()
) : Observable(), BeatSource {
    private val deviceFinder = DeviceFinder.getInstance()
    private val virtualCdj = VirtualCdj.getInstance()
    private val beatListener = BeatFinder.getInstance()
    private val metadataFinder = MetadataFinder.getInstance()
    private val analysisTagFinder = AnalysisTagFinder.getInstance()
    private val waveformFinder = WaveformFinder.getInstance()

    private var playerStates = PlayerStates()
    private val listeners = BeatLinkListeners()

    @Volatile
    var currentBeat: BeatData = BeatData(0.0, 0, confidence = 0f)

    @Volatile
    private var lastBeatAt: Time? = null

    private val currentlyAudibleChannels: MutableSet<Int> = hashSetOf()
    private val trackPositionListeners = mutableMapOf<Int, TrackPositionListener>()

    fun start() {
        logger.info { "Starting Beat Sync" }

        deviceFinder.addDeviceAnnouncementListener(object : DeviceAnnouncementListener {
            override fun deviceLost(announcement: DeviceAnnouncement) {
                val deviceNumber = announcement.deviceNumber
                logger.info { "Lost device $deviceNumber: ${announcement.deviceName}" }
                trackPositionListeners.remove(deviceNumber)?.let {
                    timeFinder.removeTrackPositionListener(it)
                }
            }

            override fun deviceFound(announcement: DeviceAnnouncement) {
                val deviceNumber = announcement.deviceNumber
                logger.info { "Found device $deviceNumber: ${announcement.deviceName}" }
                trackPositionListeners.computeIfAbsent(deviceNumber) {
                    logger.info { "Adding track position listener for device $deviceNumber" }
                    TrackPositionListener { update -> onTrackPositionUpdate(deviceNumber, update) }
                        .also { timeFinder.addTrackPositionListener(deviceNumber, it) }
                }
            }
        })

        // To find some kinds of information, like which device is the tempo master, how many beats of a track have been
        // played, or how many beats there are until the next cue point in a track, and any detailed information about
        // the tracks themselves, you need to have beat-link create a virtual player on the network. This causes the
        // other players to send detailed status updates directly to beat-link, so it can interpret and keep track of
        // this information for you.
//        virtualCdj.useStandardPlayerNumber = false
        virtualCdj.deviceNumber = 4

        virtualCdj.addLifecycleListener(object : LifecycleListener {
            override fun stopped(sender: LifecycleParticipant?) {
                logger.info { "VirtualCdj stopped!" }
            }

            override fun started(sender: LifecycleParticipant?) {
                logger.info { "VirtualCdj started as device ${virtualCdj.deviceNumber}" }
            }
        })

        beatListener.addBeatListener { beat -> newBeat(beat) }

        beatListener.addOnAirListener { audibleChannels -> channelsOnAir(audibleChannels) }

        metadataFinder.addTrackMetadataListener { update ->
            onTrackMetadata(update.player, update.metadata)
        }

        analysisTagFinder.addAnalysisTagListener({ update ->
            println("analysisChanged = $update")
        }, ".EXT", "PSSI")

        waveformFinder.addWaveformListener(object : WaveformListener {
            override fun previewChanged(update: WaveformPreviewUpdate?) {
                logger.debug { "previewChanged = $update" }
            }

            override fun detailChanged(update: WaveformDetailUpdate) {
                this@BeatLinkBeatSource.onWaveformDetailChanged(update.player, update.detail)
            }
        })

        thread(isDaemon = true, name = "BeatLinkPlugin Watchdog") {
            while (true) {
                if (!deviceFinder.isRunning) {
                    logger.info { "Attempting to start DeviceFinder..." }
                    deviceFinder.start()
                }

                if (!virtualCdj.isRunning) {
                    logger.info { "Attempting to start VirtualCdj..." }
                    virtualCdj.start()
                }

                if (!beatListener.isRunning) {
                    logger.info { "Attempting to start BeatListener..." }
                    beatListener.start()
                }

                if (!metadataFinder.isRunning) {
                    logger.info { "Attempting to start MetadataFinder..." }
                    metadataFinder.start()
                }

                if (!analysisTagFinder.isRunning) {
                    logger.info { "Attempting to start AnalysisTagFinder..." }
                    analysisTagFinder.start()
                }

                if (!waveformFinder.isRunning) {
                    logger.info { "Attempting to start WaveformFinder..." }
                    waveformFinder.start()
                }

                if (!timeFinder.isRunning) {
                    logger.info { "Attempting to start TimeFinder..." }
                    timeFinder.start()
                }

                Thread.sleep(5000)
            }
        }

        thread(isDaemon = true, name = "BeatLinkPlugin Confidence Decay") {
            while (true) {
                Thread.sleep(100)
                adjustConfidence()
            }
        }

        logger.info { "Started" }
    }

    internal fun adjustConfidence() {
        lastBeatAt?.let { lastBeatAt ->
            val nextBeatExpectedAt = lastBeatAt + currentBeat.beatIntervalMs / 1000.0 * currentBeat.beatsPerMeasure

            if (clock.now().asDoubleSeconds > nextBeatExpectedAt) {
                currentBeat = currentBeat.copy(confidence = currentBeat.confidence * .99f)

                // TODO: This is pretty MT-dodgy, refactor all this to use coroutines.
                notifyChanged()
            }
        }
    }

    override fun addListener(listener: BeatLinkListener) = listeners.addListener(listener)
    override fun removeListener(listener: BeatLinkListener) = listeners.removeListener(listener)
    private fun notifyListeners(block: (BeatLinkListener) -> Unit) = listeners.notifyListeners(block)

    private fun updatePlayerState(playerNumber: Int, block: (PlayerState) -> PlayerState) {
        playerStates = playerStates.updateWith(playerNumber, block)
        val playerState = playerStates.byDeviceNumber[playerNumber]!!
        println("playerState = $playerState")
        listeners.notifyListeners {
            it.onPlayerStateUpdate(playerNumber, playerState) }
    }

    @Synchronized
    @VisibleForTesting
    fun channelsOnAir(audibleChannels: Set<Int>) {
        val changedPlayers =
            (currentlyAudibleChannels - audibleChannels) +
                    (audibleChannels - currentlyAudibleChannels)

        currentlyAudibleChannels.clear()
        currentlyAudibleChannels.addAll(audibleChannels)

        changedPlayers.forEach { playerNumber ->
            val isOnAir = audibleChannels.contains(playerNumber)
            updatePlayerState(playerNumber) { it.copy(isOnAir = isOnAir) }
        }
    }

    private var firstBeat: Instant? = null
    private var lastBeat: Instant? = null

    @VisibleForTesting
    fun onTrackPositionUpdate(deviceNumber: Int, update: TrackPositionUpdate?) {
        logger.info { "Track position update for $deviceNumber: $update" }
        if (update != null) {
            val beatGrid: BeatGrid? = update.beatGrid
            if (beatGrid == null) {
                logger.warn { "No beat grid for device $deviceNumber." }
                return
            }

            val bpm = beatGrid.getBpm(update.beatNumber) / 100.0
            val effectiveTempo = bpm * update.pitch
            logger.info { "Beat info: $bpm$beatGrid" }

            val beatIntervalSec = 60.0 / effectiveTempo
            val beatIntervalMs = (beatIntervalSec * 1000).toInt()
            val measureStartTime = update.timestamp - beatIntervalSec * (update.beatWithinBar - 1)

            currentBeat = BeatData(measureStartTime, beatIntervalMs, confidence = 1.0f)
            logger.debug { "$deviceNumber: Setting bpm from beat ${update.beatWithinBar}" }
            notifyChanged()

            notifyListeners { it.onBeatData(currentBeat) }
        }
    }

    @Synchronized
    @VisibleForTesting
    fun newBeat(beat: Beat) {
        return
        if (firstBeat == null) {
            firstBeat = kotlinx.datetime.Clock.System.now()
        }
        val nowInstant = clock.now()
        val now = nowInstant.asDoubleSeconds
//        println("[${(nowInstant - firstBeat!!).inWholeMilliseconds}] newBeat($beat) " +
//                "at ${(nowInstant - (lastBeat ?: nowInstant)).inWholeMilliseconds}ms")
        lastBeat = nowInstant

        val deviceNumber = beat.deviceNumber

        if (
        // if more than one channel is on air, pick the tempo master
            currentlyAudibleChannels.size > 1 && beat.isTempoMaster

            // if no channels are on air, pick the master
            || currentlyAudibleChannels.isEmpty() && beat.isTempoMaster

            // one channel is on air; pick the cdj that's on it
            || currentlyAudibleChannels.size == 1 && deviceNumber == currentlyAudibleChannels.single()
        ) {
            val beatIntervalSec = 60.0 / beat.effectiveTempo
            val beatIntervalMs = (beatIntervalSec * 1000).toInt()
            val measureStartTime = now - beatIntervalSec * (beat.beatWithinBar - 1)

            println("newBeat($beat) drift=${
                "%+1.3f".format(currentBeat.measureStartTime - measureStartTime)
            }s")

            if (currentBeat.beatIntervalMs != beatIntervalMs ||
                abs(currentBeat.measureStartTime - measureStartTime) > 0.003
            ) {
                currentBeat = BeatData(measureStartTime, beatIntervalMs, confidence = 1.0f)
                logger.debug { "${beat.deviceName} on channel $deviceNumber: Setting bpm from beat ${beat.beatWithinBar}" }
                notifyChanged()

                notifyListeners { it.onBeatData(currentBeat) }

                updatePlayerState(deviceNumber) { existingPlayerState ->
                    val trackStartTime = getTrackStartTime(deviceNumber, nowInstant)
                    showChange(trackStartTime, existingPlayerState, "newBeat")
                    existingPlayerState.copy(trackStartTime = trackStartTime)
                }
            }
            lastBeatAt = now
        } else {
            logger.debug { "${beat.deviceName} on channel $deviceNumber: Ignoring beat ${beat.beatWithinBar}" }
        }
    }

    @VisibleForTesting
    fun onTrackMetadata(deviceNumber: Int, trackMetadata: TrackMetadata?) {
        println("$deviceNumber metadataChanged = $trackMetadata")
        updatePlayerState(deviceNumber) { playerState ->
            playerState.copy(
                trackTitle = trackMetadata?.title,
                trackArtist = trackMetadata?.artist?.label
            )
        }
    }

    private fun showChange(trackStartTime: Instant?, existingPlayerState: PlayerState, source: String) {
        if (trackStartTime != null) {
            existingPlayerState.trackStartTime?.run {
                println("change trackStartTime by ${this - trackStartTime} ($source)")
            }
        }
    }

    @VisibleForTesting
    fun onWaveformDetailChanged(deviceNumber: Int, detail: WaveformDetail?) {
        println("onWaveformDetailChanged($deviceNumber, $detail)")
        if (detail == null) return

        val frameCount = detail.frameCount
        val trackStartTime = getTrackStartTime(deviceNumber, clock.now())

        updatePlayerState(deviceNumber) { existingPlayerState ->
            showChange(trackStartTime, existingPlayerState, "onWaveformDetailChanged")
            existingPlayerState.withWaveform(waveformScale) {
                for (i in 0 until frameCount step waveformScale) {
                    val height = detail.segmentHeight(i, waveformScale)
                    val color = detail.segmentColor(i, waveformScale)
                    add(height, baaahs.Color(color.rgb))
                }
            }.copy(
                trackStartTime = trackStartTime
            )
        }
    }

    private fun getTrackStartTime(deviceNumber: Int, now: Instant) = if (timeFinder.isRunning) {
        val position = timeFinder.getLatestPositionFor(deviceNumber)
        if (position != null) {
            now - position.milliseconds.milliseconds / position.pitch
        } else {
            logger.warn { "No track start time for device $deviceNumber." }
            null
        }
    } else {
        // TODO: should be null; value is just for debugging STOPSHIP
        now - 200.milliseconds
    }

    companion object {
        private val logger = Logger("BeatLinkBeatSource")
        private const val waveformScale = 8
    }
}