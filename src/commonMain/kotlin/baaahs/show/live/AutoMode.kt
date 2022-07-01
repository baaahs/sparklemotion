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

    var indexSeed = 13;
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
        running = true
        autoMode = true
        gadgetWizard = CoroutineScope(Dispatchers.Unconfined).launch {
            playWithButtons()
        }
    }

    var frequency = 5000L

    private suspend fun playWithButtons() {
        val controls: List<OpenButtonControl> = openShow.allFilterButtonControls
        logger.info { "Controls " + controls.joinToString { it.gadget.title } }
        val random = Random(indexSeed++)
        var control: OpenButtonControl
        while (autoModeOn) {
            control = controls.random(random)
            // This doesn't update the UI :(
            control.click()
            logger.info { "Pushed ${control.gadget.title}" }
            delay(frequency) // TODO: Config this
        }
        logger.info { "Stopped" }
        running = false
    }

    fun stop() {
        logger.info { "Stop Called" }
        autoMode = false;
        CoroutineScope(Dispatchers.Unconfined).launch {
            gadgetWizard?.cancelAndJoin()
        }
        gadgetWizard = null
    }

    companion object {
        private val logger = Logger("AutoMode")
    }
}