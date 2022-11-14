package baaahs.gl

import baaahs.gl.patch.UnresolvedPatches
import baaahs.show.Shader
import baaahs.show.Stream
import baaahs.show.mutable.MutablePatchSet

val testToolchain = RootToolchain(testPlugins())

fun autoWire(
    vararg shaders: Shader,
    stream: Stream = Stream.Main,
    block: (UnresolvedPatches.() -> Unit)? = null
): UnresolvedPatches =
    testToolchain.autoWire(*shaders, stream = stream)
        .apply { block?.invoke(this) }

fun autoWireWithDefaults(
    vararg shaders: Shader,
    stream: Stream = Stream.Main,
    block: (UnresolvedPatches.() -> Unit)? = null
): MutablePatchSet =
    autoWire(*shaders, stream = stream, block = block)
        .acceptSuggestedLinkOptions().confirm()