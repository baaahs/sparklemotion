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
        val uvProjectorName =
            shaders.entries
                .find { (_, shaderFragment) -> shaderFragment.shaderType == ShaderFragment.Type.Projection }
                ?.key

        val links = arrayListOf<Patch.Link>()
        shaders.forEach { (name, shaderFragment) ->
            shaderFragment.inputPorts.forEach { inputPort ->
                val uniformInput =
                    if (inputPort.contentType == GlslCode.ContentType.UvCoordinate && uvProjectorName != null) {
                        { Patch.ShaderOut(uvProjectorName) }
                    } else {
                        defaultBindings[inputPort.contentType]
                    }
                        ?: { GlslProgram.UserUniformPort(inputPort.type, inputPort.name) }

                links.add(Patch.Link(uniformInput(), Patch.ShaderPort(name, inputPort.name)))
            }
        }
        return Patch(shaders, links)
    }

    private val defaultBindings = mapOf<GlslCode.ContentType, () -> Patch.UniformPort>(
        GlslCode.ContentType.UvCoordinateTexture to { GlslProgram.UvCoordsTexture },
        GlslCode.ContentType.UvCoordinate to { GlslProgram.GlFragCoord },
//            ContentType.XyCoordinate to { TODO() },
//            ContentType.XyzCoordinate to { TODO() },
//            ContentType.Color to { TODO() },
        GlslCode.ContentType.Time to { GlslProgram.Time },
        GlslCode.ContentType.Resolution to { GlslProgram.Resolution }
//            ContentType.Unknown to { TODO() }
    )
}