package baaahs.show.live

import baaahs.ShowRunner
import baaahs.control.OpenButtonControl
import baaahs.doRunBlocking
import baaahs.util.Logger
import baaahs.util.globalLaunch
import kotlin.random.Random
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class AutoMode(
    private var availableControls: List<OpenButtonControl> = emptyList(),
    private val showRunner: ShowRunner?
) {
    var autoMode = false;
    var running = false;
    val isRunning: Boolean get() = running

    val autoModeOn: Boolean get() = autoMode

    var gadgetWizard: Job? = null

    private val randomizer: Random = Random

    fun setShow(openShow: OpenShow?) {
        availableControls = openShow?.allFilterButtonControls ?: emptyList()
        if (isRunning) {
            stop()
            start()
        }
    }

    fun setState(newState: AutoModeState) {
        autoMode = newState == AutoModeState.On
        if (autoModeOn) start()
        else stop()
    }

    private fun start() {
        logger.info { "Start Called" }
        if (isRunning) return
        logger.info { "running...." }
        running = true
        gadgetWizard = CoroutineScope(Dispatchers.Unconfined).launch {
            playWithButtons()
        }
    }

    var frequency = 5000L

    private suspend fun playWithButtons() {
        var control: OpenButtonControl
        logger.info { "Controls ${availableControls.joinToString()}" }
        while (autoModeOn && availableControls.isNotEmpty()) {
            control = availableControls.random(randomizer)
            control.forceClickChange()
            logger.info { "Pushed ${control.gadget.title}" }
            delay(frequency) // TODO: Config this
        }
        logger.info { "Stopped" }
    }

    private fun stop() {
        logger.info { "Stop Called" }
        CoroutineScope(Dispatchers.Unconfined).launch {
            gadgetWizard?.cancelAndJoin()
        }
        running = false
        gadgetWizard = null
    }

    companion object {
        private val logger = Logger("AutoMode")
    }
}