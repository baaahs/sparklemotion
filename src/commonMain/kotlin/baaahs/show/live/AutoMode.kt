package baaahs.show.live

import baaahs.control.OpenButtonControl
import baaahs.doRunBlocking
import baaahs.util.globalLaunch
import kotlinx.coroutines.delay
import kotlin.random.Random


class AutoMode() {
    var availableControls: List<OpenButtonControl> = emptyList();

    var autoMode = false;
    var running = false;
    val isRunning: Boolean get() = run {
        running
    }

    fun start(newContols: List<OpenButtonControl>) {
        availableControls = newContols;
        if (isRunning) return
        autoMode = true
        doRunBlocking {
            globalLaunch {
                playWithButtons();
            }
        }
    }

    var frequency = 5000L

    private suspend fun playWithButtons() {
        running = true
        while (autoMode) {
            val randomIndex = Random.nextInt(0, availableControls.size);
            availableControls[randomIndex].gadget.adjustALittleBit();
            delay(frequency) // TODO: Config this
        }
        running = false
    }

    fun stop() {
        autoMode = false;
    }
}