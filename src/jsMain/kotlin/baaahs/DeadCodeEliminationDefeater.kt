package baaahs

import baaahs.sim.ui.Console

// Protection from dead code elimination; this method is never actually called.
fun dce() {
    // Entry points to Kotlin code that are only called by JS:
    GadgetDisplay(nuffin()) {}
    Console(nuffin())
}

@Suppress("UNCHECKED_CAST")
private fun <T> nuffin(): T = null as T
