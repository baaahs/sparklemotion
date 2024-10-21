package baaahs.gl

import baaahs.device.FixtureType
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.UnresolvedPatch
import baaahs.gl.patch.UnresolvedPatches
import baaahs.gl.shader.OpenShader
import baaahs.show.Shader
import baaahs.show.Stream
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShader

fun Toolchain.openShader(shader: Shader): OpenShader =
    openShader(analyze(shader))

fun Toolchain.openShader(mutableShader: MutableShader): OpenShader =
    openShader(analyze(mutableShader.build()))

fun Toolchain.autoWire(
    vararg shaders: Shader,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    stream: Stream = Stream.Main,
    fixtureTypes: Collection<FixtureType> = emptyList()
): UnresolvedPatches {
    val openShaders = shaders.map { openShader(it) }
    return autoWire(openShaders, defaultPorts, stream, fixtureTypes)
}

fun Toolchain.autoWire(
    shader: Shader,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    stream: Stream = Stream.Main,
    fixtureTypes: Collection<FixtureType> = emptyList()
): UnresolvedPatch {
    val openShader = openShader(shader)
    return autoWire(openShader, defaultPorts, stream, fixtureTypes)
}

fun Toolchain.autoWire(
    vararg shaders: OpenShader,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    stream: Stream = Stream.Main,
    fixtureTypes: Collection<FixtureType> = emptyList()
): UnresolvedPatches =
    autoWire(shaders.toList(), defaultPorts, stream, fixtureTypes)

fun Toolchain.withCache(name: String) = CachingToolchain(this, name)
