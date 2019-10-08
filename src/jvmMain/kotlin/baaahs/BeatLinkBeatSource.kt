package baaahs

import org.deepsymmetry.beatlink.*
import kotlin.concurrent.thread

/** Listens to the current Master CDJ's beat and tempo updates. */
class BeatLinkBeatSource(private val clock: Clock) : BeatSource, MasterListener {

    var currentBeat: BeatData = BeatData(0.0, 0, confidence = 0f)

    private val listeners = mutableListOf<(BeatData) -> Unit>()
    private var measureStartTime: Time? = null

    fun start() {
        val deviceFinder = DeviceFinder.getInstance()
        deviceFinder.start()
        deviceFinder.addDeviceAnnouncementListener(object : DeviceAnnouncementListener {
            override fun deviceLost(announcement: DeviceAnnouncement) {
                println("Beat link: Lost device: ${announcement.name}")
            }

            override fun deviceFound(announcement: DeviceAnnouncement) {
                println("Beat link: New device: ${announcement.name}")
            }
        })

        // To find some kinds of information, like which device is the tempo master, how many beats of a track have been
        // played, or how many beats there are until the next cue point in a track, and any detailed information about
        // the tracks themselves, you need to have beat-link create a virtual player on the network. This causes the
        // other players to send detailed status updates directly to beat-link, so it can interpret and keep track of
        // this information for you.
        val virtualCdj = VirtualCdj.getInstance()
        virtualCdj.useStandardPlayerNumber = true
        virtualCdj.addMasterListener(this)
        virtualCdj.addLifecycleListener(object : LifecycleListener {
            override fun stopped(sender: LifecycleParticipant?) {
                println("Beat link: VirtualCdj stopped!")
            }

            override fun started(sender: LifecycleParticipant?) {
                println("Beat link: VirtualCdj started!")
            }
        })
        virtualCdj.start()

        thread(isDaemon = true, name = "VirtualCdj watchdog") {
            while (true) {
                Thread.sleep(5000)

                if (!virtualCdj.isRunning) {
                    println("Beat link: Attempting to restart VirtualCdj...")
                    virtualCdj.start()
                }
            }
        }

        BeatFinder.getInstance().start()
    }

    override fun newBeat(beat: Beat) {
        println("Got a beat! ${beat.beatWithinBar}")
        if (beat.beatWithinBar == 1) {
            measureStartTime = clock.now()
        }
        measureStartTime?.let {
            if (beat.isTempoMaster) {
                // We currently only care about beats from the tempo master (usually, the cdj that is currently playing)
                // Could also use on-air info from mixer (it tells us which faders corresponding to which players are
                // up), which tells us which players are currently audible
                currentBeat = BeatData(it, (60_000 / beat.effectiveTempo).toInt())
                notifyListeners()
            }
        }
    }

    override fun tempoChanged(tempo: Double) {
        measureStartTime?.let {
            currentBeat = BeatData(it, (60_000 / tempo).toInt())
            notifyListeners()
        }
    }

    override fun masterChanged(update: DeviceUpdate?) {
//        println("Master CDJ changed: tempo master is now player #${update.deviceNumber}")
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