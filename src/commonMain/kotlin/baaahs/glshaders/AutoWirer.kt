package baaahs.glshaders

import baaahs.Logger
import baaahs.glsl.Shaders.cylindricalUvMapper
import baaahs.show.*
import baaahs.unknown

class AutoWirer(val plugins: Plugins) {
    private val glslAnalyzer = GlslAnalyzer()

    fun autoWire(colorShader: String): PatchEditor {
        return autoWire(glslAnalyzer.asShader(colorShader) as ColorShader)
    }

    fun autoWire(colorShader: ColorShader): PatchEditor {
        return autoWire(cylindricalUvMapper, colorShader)
            .resolve()
    }

    fun autoWire(vararg shaders: Shader): UnresolvedPatchEditor {
        return autoWire(shaders.map { glslAnalyzer.asShader(it.src) })
    }

    fun autoWire(vararg shaders: OpenShader): UnresolvedPatchEditor {
        return autoWire(shaders.toList())
    }

    fun autoWire(shaders: List<OpenShader>): UnresolvedPatchEditor {
        val locallyAvailable: MutableMap<ContentType, MutableList<LinkEditor.Port>> = mutableMapOf()

        // First pass: gather shader output ports.
        val shaderInstances = shaders.associate { openShader ->
            val role = openShader.shaderType.primaryRole
            val unresolvedShaderInstance = UnresolvedShaderInstanceEditor(
                ShaderEditor(openShader.shader),
                openShader.inputPorts.map { it.id }.associateWith { arrayListOf<LinkEditor.Port>() },
                role = role
            )

            val options = locallyAvailable.getOrPut(openShader.outputPort.contentType) { mutableListOf() }
            options.add(UnresolvedShaderOutPortEditor(unresolvedShaderInstance, openShader.outputPort.id))

            openShader to unresolvedShaderInstance
        }

        // Second pass: link datasources/output ports to input ports.
        val dataSources = hashSetOf<DataSource>()
        val unresolvedShaderInstances = shaderInstances.map { (openShader, unresolvedShaderInstance) ->
            openShader.inputPorts.forEach { inputPort ->
                val localSuggestions: List<LinkEditor.Port>? = locallyAvailable[inputPort.contentType]
                val suggestions = localSuggestions ?: plugins.suggestDataSources(inputPort).map {
                    dataSources.add(it)
                    DataSourceEditor(it)
                }
                unresolvedShaderInstance.incomingLinksOptions[inputPort.id]!!.addAll(suggestions)
            }
            unresolvedShaderInstance
        }
        return UnresolvedPatchEditor(unresolvedShaderInstances, dataSources.toList())
    }

    data class UnresolvedShaderOutPortEditor(
        val unresolvedShaderInstance: UnresolvedShaderInstanceEditor,
        val portId: String
    ) : LinkEditor.Port {
        override fun toRef(showBuilder: ShowBuilder): PortRef = TODO("not implemented")
        override fun displayName(): String = TODO("not implemented")
    }

    data class UnresolvedShaderInstanceEditor(
        val shader: ShaderEditor,
        val incomingLinksOptions: Map<String, MutableList<LinkEditor.Port>>,
        var role: ShaderRole? = null
    ) {
        fun isAmbiguous() = incomingLinksOptions.values.any { it.size > 1 }
    }

    data class UnresolvedPatchEditor(
        private val unresolvedShaderInstances: List<UnresolvedShaderInstanceEditor>,
        private val dataSources: List<DataSource>
    ) {
        fun isAmbiguous() = unresolvedShaderInstances.any { it.isAmbiguous() }

        fun resolve(): PatchEditor {
            if (isAmbiguous()) {
                error("ambiguous! ${unresolvedShaderInstances.filter { it.isAmbiguous() }}")
            }

            // First pass: create a shader instance editor for each shader.
            val shaderInstances = unresolvedShaderInstances.associate {
                it.shader.shader to ShaderInstanceEditor(
                    it.shader,
                    it.incomingLinksOptions.mapValues { (_, fromPortOptions) ->
                        fromPortOptions.first()
                    }.toMutableMap(),
                    it.role
                )
            }

            // Second pass: resolve references between shaders to the correct instance editor.
            shaderInstances.values.forEach { shaderInstance ->
                shaderInstance.incomingLinks.forEach { (toPortId, fromPort) ->
                    if (fromPort is UnresolvedShaderOutPortEditor) {
                        val fromShader = fromPort.unresolvedShaderInstance.shader.shader
                        val fromShaderInstance = shaderInstances[fromShader]
                            ?: error(unknown("shader instance editor", fromShader, shaderInstances.keys))
                        shaderInstance.incomingLinks[toPortId] = ShaderOutPortEditor(fromShaderInstance, fromPort.portId)
                    }
                }
            }

            return PatchEditor(shaderInstances.values.toList(), Surfaces.AllSurfaces)
        }
    }

    companion object {
        private val logger = Logger("AutoWirer")
    }
}