package baaahs.gl

import baaahs.device.FixtureType
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.UnresolvedPatch
import baaahs.gl.shader.OpenShader
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShader

fun Toolchain.openShader(shader: Shader): OpenShader =
    openShader(analyze(shader))

fun Toolchain.openShader(mutableShader: MutableShader): OpenShader =
    openShader(analyze(mutableShader.build()))

fun Toolchain.autoWire(
    vararg shaders: Shader,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    shaderChannel: ShaderChannel = ShaderChannel.Main,
    fixtureTypes: Collection<FixtureType> = emptyList()
): UnresolvedPatch {
    val openShaders = shaders.map { openShader(it) }
    return autoWire(openShaders, defaultPorts, shaderChannel, fixtureTypes)
}

fun Toolchain.autoWire(
    vararg shaders: OpenShader,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    shaderChannel: ShaderChannel = ShaderChannel.Main,
    fixtureTypes: Collection<FixtureType> = emptyList()
): UnresolvedPatch {
    return autoWire(shaders.toList(), defaultPorts, shaderChannel, fixtureTypes)
}

fun Toolchain.withCache(name: String) = CachingToolchain(this, name)
