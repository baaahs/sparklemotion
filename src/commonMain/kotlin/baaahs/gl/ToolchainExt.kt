package baaahs.gl

import baaahs.fixtures.DeviceType
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.UnresolvedPatch
import baaahs.gl.shader.OpenShader
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutablePort

fun Toolchain.openShader(shader: Shader): OpenShader = openShader(analyze(shader))

fun Toolchain.autoWire(
    vararg shaders: Shader,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    shaderChannel: ShaderChannel = ShaderChannel.Main,
    deviceTypes: Collection<DeviceType> = emptyList()
): UnresolvedPatch {
    val openShaders = shaders.map { openShader(it) }
    return autoWire(openShaders, defaultPorts, shaderChannel, deviceTypes)
}

fun Toolchain.autoWire(
    vararg shaders: OpenShader,
    defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
    shaderChannel: ShaderChannel = ShaderChannel.Main,
    deviceTypes: Collection<DeviceType> = emptyList()
): UnresolvedPatch {
    return autoWire(shaders.toList(), defaultPorts, shaderChannel, deviceTypes)
}

fun Toolchain.withCache(name: String) = CachingToolchain(this, name)
