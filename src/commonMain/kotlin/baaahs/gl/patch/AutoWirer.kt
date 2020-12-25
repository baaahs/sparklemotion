package baaahs.gl.patch

import baaahs.fixtures.DeviceType
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.Surfaces
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.OpenPatch
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShader

class AutoWirer(
    val plugins: Plugins,
    val glslAnalyzer: GlslAnalyzer = GlslAnalyzer(plugins)
) {
    fun autoWire(
        vararg shaders: Shader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        deviceTypes: Collection<DeviceType> = emptyList()
    ): UnresolvedPatch {
        val openShaders = shaders.associate { it to glslAnalyzer.openShader(it) }
        return autoWire(openShaders.values, shaderChannel, defaultPorts, deviceTypes)
    }

    fun autoWire(
        vararg shaders: OpenShader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        deviceTypes: Collection<DeviceType> = emptyList()
    ): UnresolvedPatch {
        return autoWire(shaders.toList(), shaderChannel, defaultPorts, deviceTypes)
    }

    fun autoWire(
        shaders: Collection<OpenShader>,
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        deviceTypes: Collection<DeviceType> = emptyList()
    ): UnresolvedPatch {
        val siblingsPatch = OpenPatch(shaders.map {
            LiveShaderInstance(it, emptyMap(), shaderChannel, 0f)
        }, Surfaces.AllSurfaces)

        val parentShow = null as OpenShow? // TODO: test with non-null?
        val channelsInfo = ChannelsInfo(
            parentShow, if (deviceTypes.isEmpty()) plugins.deviceTypes.all else deviceTypes
        )

        // First pass: gather shader output ports.
        val shaderInstances =
            shaders.associateWith { openShader ->
                val shaderInstanceOptions =
                    ShaderInstanceOptions(openShader, shaderChannel, channelsInfo, defaultPorts, emptyMap(), plugins)

                val unresolvedShaderInstance = UnresolvedShaderInstance(
                    MutableShader(openShader.shader),
                    openShader.inputPorts.associateWith { inputPort ->
                        shaderInstanceOptions.inputPortLinkOptions[inputPort.id]?.toMutableList() ?: mutableListOf()
                    },
                    shaderChannel,
                    0f
                )

                unresolvedShaderInstance
            }

        return UnresolvedPatch(shaderInstances.values.toList())
    }
}