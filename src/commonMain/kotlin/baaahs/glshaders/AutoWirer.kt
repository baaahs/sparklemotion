package baaahs.glshaders

import baaahs.glsl.GlslRenderer
import baaahs.ports.*
import baaahs.show.DataSource

class AutoWirer(val plugins: Plugins) {
    fun autoWire(colorShader: String, shaderId: String = "color"): Patch {
        return autoWire(
            GlslRenderer.glslAnalyzer.asShader(colorShader) as ColorShader,
            shaderId
        )
    }

    fun autoWire(colorShader: ColorShader, shaderId: String = "color"): Patch {
        return autoWire(mapOf(
            "uv" to GlslRenderer.uvMapper,
            shaderId to colorShader
        )).resolve()
    }

    fun autoWire(shaders: Map<String, ShaderFragment>): UnresolvedPatch {
        val locallyAvailable: MutableMap<ContentType, MutableList<ShaderOutPortRef>> = mutableMapOf()

        shaders.entries.forEach { (shaderId, shaderFragment) ->
            val contentType = shaderFragment.shaderType.outContentType
            val options = locallyAvailable.getOrPut(contentType) { mutableListOf() }
            options.add(ShaderOutPortRef(shaderId))
        }
        locallyAvailable.remove(ContentType.Color) // TODO: why?

        val dataSources = hashSetOf<DataSource>()
        val linkOptions = shaders.flatMap { (name, shaderFragment) ->
            shaderFragment.inputPorts.map { inputPort ->
                val localSuggestions: List<PortRef>? = locallyAvailable[inputPort.contentType]
                val suggestions =
                    localSuggestions ?: plugins.suggestDataSources(inputPort).map {
                        dataSources.add(it)
                        DataSourceRef(it.id)
                    }

                UnresolvedPatch.LinkOptions(suggestions, ShaderInPortRef(name, inputPort.id))
            }
        }
        return UnresolvedPatch(shaders, dataSources.toList(), linkOptions)
    }

    data class UnresolvedPatch(
        private val shaders: Map<String, ShaderFragment>,
        private val dataSources: List<DataSource>,
        private val linkOptions: List<LinkOptions>
    ) {
        fun isAmbiguous() = linkOptions.any { it.isAmbiguous() }

        fun resolve(): Patch {
            if (isAmbiguous()) {
                error("ambiguous! ${linkOptions.filter { it.isAmbiguous() }}")
            }
            return Patch(shaders, dataSources, linkOptions.map { Link(it.from.first(), it.to) })
        }

        data class LinkOptions(val from: List<PortRef>, val to: PortRef) {
            fun isAmbiguous() = from.size != 1
        }
    }
}