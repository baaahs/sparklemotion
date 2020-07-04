package baaahs

import baaahs.sim.ui.Console
import baaahs.sim.ui.WebClientWindow
import baaahs.visualizer.ui.VisualizerPanel

object DeadCodeEliminationDefeater {
    // Protection from dead code elimination; this method is never actually called.
    fun noDCE() {
        // Entry points to Kotlin code that are only called by JS:
        GadgetDisplay(nuffin()) {}
        Console(nuffin())
        VisualizerPanel(nuffin())
        WebClientWindow(nuffin())
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> nuffin(): T = null as T
}
