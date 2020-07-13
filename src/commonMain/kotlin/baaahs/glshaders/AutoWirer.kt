package baaahs.glshaders

import baaahs.Logger
import baaahs.getBang
import baaahs.glsl.Shaders.cylindricalUvMapper
import baaahs.show.*

class AutoWirer(val plugins: Plugins) {
    private val glslAnalyzer = GlslAnalyzer()

    fun autoWire(colorShader: String): PatchEditor {
        return autoWire(glslAnalyzer.asShader(colorShader) as ColorShader)
    }

    fun autoWire(colorShader: ColorShader): PatchEditor {
        return autoWire(cylindricalUvMapper, colorShader)
            .resolve()
    }

    fun autoWire(vararg shaders: OpenShader): UnresolvedPatchEditor {
        val shaderEditors = shaders.associate { it.shader to it.shader.edit() }
        val locallyAvailable: MutableMap<ContentType, MutableList<LinkEditor.Port>> = mutableMapOf()

        shaders.forEach { openShader ->
            val shaderEditor = shaderEditors.getBang(openShader.shader, "shader editor")
            openShader.outputPorts.forEach { outputPort ->
                val options = locallyAvailable.getOrPut(outputPort.contentType) { mutableListOf() }
                options.add(shaderEditor.outputPort(outputPort.name))
            }
        }

        val dataSources = hashSetOf<DataSource>()
        val linkOptions = shaders.flatMap { openShader ->
            val shaderEditor = shaderEditors.getBang(openShader.shader, "shader editor")

            openShader.inputPorts.map { inputPort ->
                val localSuggestions: List<LinkEditor.Port>? = locallyAvailable[inputPort.contentType]
                val suggestions: List<LinkEditor.Port> =
                    localSuggestions ?: plugins.suggestDataSources(inputPort).map {
                        dataSources.add(it)
                        DataSourceEditor(it)
                    }

                UnresolvedPatchEditor.LinkOptions(
                    suggestions,
                    shaderEditor.inputPort(inputPort.id)
                )
            }
        } + UnresolvedPatchEditor.LinkOptions(
            locallyAvailable[ContentType.Color] ?: emptyList(),
            OutputPortEditor(GlslProgram.PixelColor.portId)
        )
        return UnresolvedPatchEditor(shaders.toList(), dataSources.toList(), linkOptions)
    }

    data class UnresolvedPatchEditor(
        private val shaders: List<OpenShader>,
        private val dataSources: List<DataSource>,
        private val linkOptions: List<LinkOptions>
    ) {
        fun isAmbiguous() = linkOptions.any { it.isAmbiguous() }

        fun resolve(): PatchEditor {
            if (isAmbiguous()) {
                error("ambiguous! ${linkOptions.filter { it.isAmbiguous() }}")
            }
            return PatchEditor(
                linkOptions.map { (from, to) ->
                    LinkEditor(from.first(), to)
                },
                Surfaces.AllSurfaces
            )
        }

        data class LinkOptions(val from: List<LinkEditor.Port>, val to: LinkEditor.Port) {
            fun isAmbiguous() = from.size > 1
        }
    }

    companion object {
        private val logger = Logger("AutoWirer")
    }
}