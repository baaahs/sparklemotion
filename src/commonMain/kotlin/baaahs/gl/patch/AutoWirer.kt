package baaahs.gl.patch

import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShow

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
        return autoWire(openShaders.values, defaultPorts = defaultPorts, shaderChannel = shaderChannel)
    }

    fun autoWire(
        vararg shaders: OpenShader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        shaderChannel: ShaderChannel = ShaderChannel.Main
        ): UnresolvedPatch {
        return autoWire(shaders.toList(), defaultPorts = defaultPorts, shaderChannel = shaderChannel)
    }

    fun autoWire(
        shaders: Collection<OpenShader>,
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        parentMutableShow: MutableShow? = null,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap()
    ): UnresolvedPatch {
        val siblingsPatch = MutablePatch {
            shaders.forEach { addShaderInstance(it.shader) }
        }

        // First pass: gather shader output ports.
        val shaderInstances =
            shaders.associateWith { openShader ->
                val shaderInstanceOptions = ShaderInstanceOptions(
                    openShader,
// TODO                   parentMutableShow = parentMutableShow,
                    parentMutablePatch = siblingsPatch,
                    defaultPorts = defaultPorts,
                    currentLinks = emptyMap(),
                    glslAnalyzer = glslAnalyzer,
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

    fun autoWire(
        shader: OpenShader,
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        parentMutableShow: MutableShow? = null,
        parentMutablePatch: MutablePatch? = null,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        currentLinks: Map<String, MutablePort> = emptyMap()
    ): ShaderInstanceOptions = ShaderInstanceOptions(
        shader,
        shaderChannel,
        parentMutableShow,
        parentMutablePatch,
        defaultPorts,
        currentLinks,
        glslAnalyzer,
        plugins
    )
}