package baaahs.gl.patch

import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.Surfaces
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.OpenPatch
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShader

class AutoWirer(
    val plugins: Plugins,
    val glslAnalyzer: GlslAnalyzer = GlslAnalyzer(plugins)
) {
    fun autoWire(
        vararg shaders: Shader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        shaderChannel: ShaderChannel = ShaderChannel.Main
    ): UnresolvedPatch {
        val openShaders = shaders.associate { it to glslAnalyzer.openShader(it) }
        return autoWire(openShaders.values, shaderChannel = shaderChannel, defaultPorts = defaultPorts)
    }

    fun autoWire(
        vararg shaders: OpenShader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        shaderChannel: ShaderChannel = ShaderChannel.Main
        ): UnresolvedPatch {
        return autoWire(shaders.toList(), shaderChannel = shaderChannel, defaultPorts = defaultPorts)
    }

    fun autoWire(
        shaders: Collection<OpenShader>,
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap()
    ): UnresolvedPatch {
        val siblingsPatch = OpenPatch(shaders.map {
            LiveShaderInstance(it, emptyMap(), shaderChannel, 0f)
        }, Surfaces.AllSurfaces)

        val channelsInfo = ChannelsInfo(
            null, // TODO: test with parentMutableShow?
            siblingsPatch)

        // First pass: gather shader output ports.
        val shaderInstances =
            shaders.associateWith { openShader ->
                val shaderInstanceOptions = ShaderInstanceOptions(
                    openShader,
                    channelsInfo = channelsInfo,
                    defaultPorts = defaultPorts,
                    currentLinks = emptyMap(),
                    plugins = plugins
                )

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