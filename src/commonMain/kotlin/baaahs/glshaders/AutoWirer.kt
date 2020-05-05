package baaahs.glshaders

import baaahs.glsl.GlslRenderer

class AutoWirer {
    fun autoWire(colorShader: String): Patch {
        return autoWire(GlslRenderer.glslAnalyzer.asShader(colorShader) as ColorShader)
    }

    fun autoWire(colorShader: ColorShader): Patch {
        return autoWire(mapOf(
            "uv" to GlslRenderer.uvMapper,
            "color" to colorShader
        ))
    }

    fun autoWire(shaders: Map<String, ShaderFragment>): Patch {
        val localDefaults = shaders.entries.associate { (shaderId, shaderFragment) ->
            shaderFragment.shaderType.outContentType to Patch.ShaderOut(shaderId)
        }.minus(GlslCode.ContentType.Color)

        val links = arrayListOf<Patch.Link>()
        shaders.forEach { (name, shaderFragment) ->
            shaderFragment.inputPorts.forEach { inputPort ->
                val uniformInput =
                    localDefaults[inputPort.contentType]
                        ?: defaultBindings[inputPort.contentType]
                        ?: GlslProgram.InputPortRef(
                            inputPort.type, inputPort.name, inputPort.pluginId, inputPort.pluginConfig)

                links.add(Patch.Link(uniformInput, Patch.ShaderPortRef(name, inputPort.name)))
            }
        }
        return Patch(shaders, links)
    }

    private val defaultBindings = mapOf<GlslCode.ContentType, Patch.UniformPortRef>(
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