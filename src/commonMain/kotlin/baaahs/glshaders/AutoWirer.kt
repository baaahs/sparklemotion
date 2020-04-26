package baaahs.glshaders

import baaahs.glsl.GlslRenderer

class AutoWirer {
    fun autoWire(colorShader: String): Patch {
        return autoWire(GlslRenderer.glslAnalyzer.asShader(colorShader) as ShaderFragment.ColorShader)
    }

    fun autoWire(colorShader: ShaderFragment.ColorShader): Patch {
        return autoWire(mapOf(
            "uv" to GlslRenderer.uvMapper,
            "color" to colorShader
        ))
    }

    fun autoWire(shaders: Map<String, ShaderFragment>): Patch {
        val uvProjectorName =
            shaders.entries
                .find { (_, shaderFragment) -> shaderFragment.shaderType == ShaderType.Projection }
                ?.key

        val links = arrayListOf<Link>()
        shaders.forEach { (name, shaderFragment) ->
            shaderFragment.inputPorts.forEach { inputPort ->
                val uniformInput =
                    if (inputPort.contentType == GlslCode.ContentType.UvCoordinate && uvProjectorName != null) {
                        { GlslProgram.ShaderOut(uvProjectorName) }
                    } else {
                        defaultBindings[inputPort.contentType]
                    }
                        ?: { GlslProgram.UserUniformInput(inputPort.type, inputPort.name) }

                links.add(uniformInput() to GlslProgram.ShaderPort(name, inputPort.name))
            }
        }
        return Patch(shaders, links)
    }

    private val defaultBindings = mapOf<GlslCode.ContentType, () -> GlslProgram.UniformInput>(
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