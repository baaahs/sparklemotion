package baaahs.plugin.beatlink

import baaahs.ui.Observable
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import baaahs.util.asDoubleSeconds
import org.deepsymmetry.beatlink.*
import kotlin.concurrent.thread
import kotlin.math.abs

/**
 * Listens to all connected CDJs' beat and tempo updates.
 *
 * We pick the CDJ to sync shows to based on the following priority:
 * 1) The CDJ that is currently on-air (has its fader up on the mixer)
 * 2) If more than one CDJ is on-air, pick the CDJ that is the Tempo Master
 */
class BeatLinkBeatSource(
    private val clock: Clock
) : Observable(), BeatSource, BeatListener, OnAirListener {

    @Volatile
    var currentBeat: BeatData = BeatData(0.0, 0, confidence = 0f)

    private val logger = Logger("BeatLinkBeatSource")
    private val currentlyAudibleChannels: MutableSet<Int> = hashSetOf()
    @Volatile
    private var lastBeatAt: Time? = null

    fun start() {
        logger.info { "Starting Beat Sync" }
        val deviceFinder = DeviceFinder.getInstance()
        deviceFinder.addDeviceAnnouncementListener(object : DeviceAnnouncementListener {
            override fun deviceLost(announcement: DeviceAnnouncement) {
                logger.info { "Lost device: ${announcement.deviceName}" }
            }

            override fun deviceFound(announcement: DeviceAnnouncement) {
                logger.info { "New device: ${announcement.deviceName}" }
            }
        })

        // To find some kinds of information, like which device is the tempo master, how many beats of a track have been
        // played, or how many beats there are until the next cue point in a track, and any detailed information about
        // the tracks themselves, you need to have beat-link create a virtual player on the network. This causes the
        // other players to send detailed status updates directly to beat-link, so it can interpret and keep track of
        // this information for you.
        val virtualCdj = VirtualCdj.getInstance()
        virtualCdj.useStandardPlayerNumber = false
        virtualCdj.addLifecycleListener(object : LifecycleListener {
            override fun stopped(sender: LifecycleParticipant?) {
                logger.info { "VirtualCdj stopped!" }
            }

            override fun started(sender: LifecycleParticipant?) {
                logger.info { "VirtualCdj started as device ${virtualCdj.deviceNumber}" }
            }
        })

        val beatListener = BeatFinder.getInstance()
        beatListener.addBeatListener(this)
        beatListener.addOnAirListener(this)

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

    override fun channelsOnAir(audibleChannels: MutableSet<Int>?) {
        currentlyAudibleChannels.clear()
        audibleChannels?.let { currentlyAudibleChannels.addAll(it) }
    }

    override fun newBeat(beat: Beat) {
        if (
            // if more than one channel is on air, pick the tempo master
            currentlyAudibleChannels.size > 1 && beat.isTempoMaster

            // if no channels are on air, pick the master
            || currentlyAudibleChannels.isEmpty() && beat.isTempoMaster

            // one channel is on air; pick the cdj that's on it
            || currentlyAudibleChannels.size == 1 && beat.deviceNumber == currentlyAudibleChannels.single()
        ) {
            val beatIntervalSec = 60.0 / beat.effectiveTempo
            val beatIntervalMs = (beatIntervalSec * 1000).toInt()
            val now = clock.now().asDoubleSeconds
            val measureStartTime = now - beatIntervalSec * (beat.beatWithinBar - 1)
            if (currentBeat.beatIntervalMs != beatIntervalMs ||
                abs(currentBeat.measureStartTime - measureStartTime) > 0.003
            ) {
                currentBeat = BeatData(measureStartTime, beatIntervalMs, confidence = 1.0f)
                logger.debug { "${beat.deviceName} on channel ${beat.deviceNumber}: Setting bpm from beat ${beat.beatWithinBar}" }
                notifyChanged()
            }
            lastBeatAt = now
        } else {
            logger.debug { "${beat.deviceName} on channel ${beat.deviceNumber}: Ignoring beat ${beat.beatWithinBar}" }
        }
    }

    override fun getBeatData(): BeatData {
        return currentBeat
    }
}