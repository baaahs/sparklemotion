package baaahs

import baaahs.glsl.GlslPreview
import baaahs.sim.ui.Console

// Protection from dead code elimination; this method is never actually called.
fun dce() {
    // Entry points to Kotlin code that are only called by JS:
    GadgetDisplay(nuffin()) {}
    GlslPreview(nuffin(), nuffin(), null)
    Console(nuffin())
}

@Suppress("UNCHECKED_CAST")
private fun <T> nuffin(): T = null as T
