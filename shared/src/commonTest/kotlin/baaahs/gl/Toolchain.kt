package baaahs.gl

import baaahs.gl.patch.ContentType
import baaahs.gl.patch.UnresolvedPatches
import baaahs.show.Shader
import baaahs.show.Stream
import baaahs.show.mutable.MutablePatchSet
import baaahs.show.mutable.MutablePort

val testToolchain = RootToolchain(testPlugins())

fun autoWire(
    vararg shaders: Shader,
    stream: Stream = Stream.Main,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    block: (UnresolvedPatches.() -> Unit)? = null
): UnresolvedPatches =
    testToolchain.autoWire(*shaders, stream = stream, defaultPorts = defaultPorts)
        .apply { block?.invoke(this) }

fun autoWireWithDefaults(
    vararg shaders: Shader,
    stream: Stream = Stream.Main,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    block: (UnresolvedPatches.() -> Unit)? = null
): MutablePatchSet =
    autoWire(*shaders, stream = stream, defaultPorts = defaultPorts, block = block)
        .acceptSuggestedLinkOptions().confirm()