package baaahs.show.live

import baaahs.control.OpenButtonControl
import baaahs.doRunBlocking
import baaahs.util.Logger
import baaahs.util.globalLaunch
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class AutoMode(
    private val openShow: OpenShow
) {

    var autoMode = false;
    var running = false;
    val isRunning: Boolean get() = run {
        running
    }

    val autoModeOn: Boolean get() = run {
        autoMode
    }

    var gadgetWizard: Job? = null

    fun start() {
        if (isRunning) return
        autoMode = true
        gadgetWizard = CoroutineScope(Dispatchers.Main).launch {
            running = true
            playWithButtons()
        }
    }

    var frequency = 5000L

    private suspend fun playWithButtons() {
        var randomIndex = 0
        var controls: List<OpenButtonControl> = openShow.allFilterButtonControls
        logger.info { "Controls " + controls.joinToString { it.gadget.title } }
        while (autoModeOn) {
            controls = openShow.allFilterButtonControls
            randomIndex = Random.nextInt(0, controls.size);
            controls[randomIndex].gadget.enabled = !controls[randomIndex].gadget.enabled
            logger.info { "Pushed $randomIndex ${controls[randomIndex].gadget.title}" }
            delay(frequency) // TODO: Config this
        }
        logger.info { "Stopped" }
        running = false
    }

    fun stop(): AutoMode {
        logger.info { "Stop Called" }
        autoMode = false;
        return this
    }

    fun clean() {
        GlobalScope.launch {
            gadgetWizard?.join()
        }
        gadgetWizard = null
    }

    companion object {
        private val logger = Logger("AutoMode")
    }
}