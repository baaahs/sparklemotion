package baaahs.glshaders

import baaahs.glsl.GlslRenderer
import baaahs.ports.*

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
        ))
    }

    fun autoWire(shaders: Map<String, ShaderFragment>): Patch {
        val localDefaults = shaders.entries.associate { (shaderId, shaderFragment) ->
            shaderFragment.shaderType.outContentType to ShaderOutPortRef(shaderId)
        }.minus(GlslCode.ContentType.Color)

        val links = arrayListOf<Link>()
        shaders.forEach { (name, shaderFragment) ->
            shaderFragment.inputPorts.forEach { inputPort ->
                val uniformInput =
                    localDefaults[inputPort.contentType]
                        ?: defaultBindings[inputPort.contentType]
                        ?: suggestInputPortRef(inputPort)
                val canonicalPortRef =
                    when (uniformInput) {
                        is InputPortRef -> plugins.validateAndCanonicalize(uniformInput)
                        else -> uniformInput
                    }

                links.add(Link(canonicalPortRef, ShaderInPortRef(name, inputPort.id)))
            }
        }
        return Patch(shaders, links)
    }

    private fun suggestInputPortRef(inputPort: InputPort): InputPortRef {
        val postfix = PluginRef(inputPort.pluginId).resource
        val id = inputPort.id.decapitalize() + postfix.capitalize()
        return inputPortRef(
            id,
            inputPort.type,
            inputPort.title,
            inputPort.pluginId,
            inputPort.pluginConfig,
            "in_$id"
        )
    }

    private val defaultBindings = mapOf(
        GlslCode.ContentType.UvCoordinateTexture to GlslProgram.UvCoordsTexture,
        GlslCode.ContentType.UvCoordinate to GlslProgram.GlFragCoord,
//            ContentType.XyCoordinate to { TODO() },
//            ContentType.XyzCoordinate to { TODO() },
//            ContentType.Color to { TODO() },
        GlslCode.ContentType.Time to GlslProgram.Time,
        GlslCode.ContentType.Resolution to GlslProgram.Resolution
//            ContentType.Unknown to { TODO() }
    )
}