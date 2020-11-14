package baaahs.plugin.beatlink

import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
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
class BeatLinkBeatSource(private val clock: Clock) : BeatSource, BeatListener, OnAirListener {

    var currentBeat: BeatData = BeatData(0.0, 0, confidence = 0f)

    private val logger = Logger("BeatLinkBeatSource")
    private val currentlyAudibleChannels: MutableSet<Int> = hashSetOf()
    private val listeners = mutableListOf<(BeatData) -> Unit>()
    private var lastBeatAt: Time? = null

    fun start() {
        logger.info { "Starting Beat Sync" }
        val deviceFinder = DeviceFinder.getInstance()
        deviceFinder.start()
        deviceFinder.addDeviceAnnouncementListener(object : DeviceAnnouncementListener {
            override fun deviceLost(announcement: DeviceAnnouncement) {
                logger.info { "Lost device: ${announcement.name}" }
            }

            override fun deviceFound(announcement: DeviceAnnouncement) {
                logger.info { "New device: ${announcement.name}" }
            }
        })

        // To find some kinds of information, like which device is the tempo master, how many beats of a track have been
        // played, or how many beats there are until the next cue point in a track, and any detailed information about
        // the tracks themselves, you need to have beat-link create a virtual player on the network. This causes the
        // other players to send detailed status updates directly to beat-link, so it can interpret and keep track of
        // this information for you.
        val virtualCdj = VirtualCdj.getInstance()
        virtualCdj.useStandardPlayerNumber = true
        virtualCdj.addLifecycleListener(object : LifecycleListener {
            override fun stopped(sender: LifecycleParticipant?) {
                logger.info { "VirtualCdj stopped!" }
            }

            override fun started(sender: LifecycleParticipant?) {
                logger.info { "VirtualCdj started as device ${virtualCdj.deviceNumber}" }
            }
        })
        virtualCdj.start()

        thread(isDaemon = true, name = "VirtualCdj watchdog") {
            while (true) {
                Thread.sleep(5000)

                if (!virtualCdj.isRunning) {
                    logger.info { "Attempting to restart VirtualCdj..." }
                    virtualCdj.start()
                }
            }
        }

        thread(isDaemon = true, name = "Beat confidence decay") {
            while (true) {
                Thread.sleep(32)

                lastBeatAt?.let {
                    if (it < clock.now() - currentBeat.beatIntervalMs * currentBeat.beatsPerMeasure) {
                        currentBeat = currentBeat.copy(confidence = currentBeat.confidence * .9f)
                    }
                }
            }
        }

        val beatListener = BeatFinder.getInstance()
        beatListener.addBeatListener(this)
        beatListener.addOnAirListener(this)
        beatListener.start()
        logger.info { "Started" }
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
            val now = clock.now()
            val measureStartTime = now - beatIntervalSec * (beat.beatWithinBar - 1)
            if (currentBeat.beatIntervalMs != beatIntervalMs ||
                abs(currentBeat.measureStartTime - measureStartTime) > 0.003
            ) {
                currentBeat = BeatData(measureStartTime, beatIntervalMs, confidence = 1.0f)
                logger.debug { "${beat.deviceName} on channel ${beat.deviceNumber}: Setting bpm from beat ${beat.beatWithinBar}" }
                notifyListeners()
            }
            lastBeatAt = now
        } else {
            logger.debug { "${beat.deviceName} on channel ${beat.deviceNumber}: Ignoring beat ${beat.beatWithinBar}" }
        }
    }

    override fun getBeatData(): BeatData {
        return currentBeat
    }

    fun listen(callback: (BeatData) -> Unit) {
        listeners.add(callback)
    }

    private fun notifyListeners() {
        listeners.forEach { it(currentBeat) }
    }
}