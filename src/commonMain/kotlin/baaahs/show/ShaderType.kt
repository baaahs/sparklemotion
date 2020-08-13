package baaahs.show

import baaahs.gl.glsl.GlslCode
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.*
import baaahs.show.mutable.MutableShader

enum class ShaderType(
    val priority: Int,
    val defaultUpstreams: Map<ContentType, ShaderChannel>,
    val resultContentType: ContentType,
    val template: String
) {
    Projection(
        0, emptyMap(), ContentType.UvCoordinateStream, """
            uniform sampler2D pixelCoordsTexture;
            
            struct ModelInfo {
                vec3 center;
                vec3 extents;
            };
            uniform ModelInfo modelInfo;

            vec2 project(vec3 pixelLocation) {
                vec3 start = modelInfo.center - modelInfo.extents / 2.;
                vec3 rel = (pixelLocation - start) / modelInfo.extents;
                return rel.xy;
            }
            
            vec2 mainProjection(vec2 rasterCoord) {
                int rasterX = int(rasterCoord.x);
                int rasterY = int(rasterCoord.y);
                
                vec3 pixelCoord = texelFetch(pixelCoordsTexture, ivec2(rasterX, rasterY), 0).xyz;
                return project(pixelCoord);
            }
    """.trimIndent()
    ) {
        override fun matches(glslCode: GlslCode) =
            glslCode.functionNames.contains("mainProjection")

        override fun open(shader: Shader, glslCode: GlslCode) =
            ProjectionShader(shader, glslCode)
    },

    Distortion(
        1,
        mapOf(ContentType.UvCoordinateStream to ShaderChannel.Main),
        ContentType.UvCoordinateStream, """
            uniform float size; // @@Slider min=0.75 max=1.25 default=1
    
            vec2 mainDistortion(vec2 uvIn) {
              return (uvIn - .5)  * size + .5;
            }
        """.trimIndent()
    ) {
        override fun matches(glslCode: GlslCode) =
            glslCode.functionNames.contains("mainDistortion")

        override fun open(shader: Shader, glslCode: GlslCode) =
            DistortionShader(shader, glslCode)
    },

    Paint(
        3,
        mapOf(ContentType.UvCoordinateStream to ShaderChannel.Main),
        ContentType.ColorStream, """
            uniform float time;
    
            void main(void) {
                gl_FragColor = vec4(gl_FragCoord.x, gl_FragCoord.y, mod(time, 1.), 1.);
            }
        """.trimIndent()
    ) {
        override fun matches(glslCode: GlslCode): Boolean {
            return glslCode.functionNames.contains("main") ||
                    glslCode.functionNames.contains("mainImage")
        }

        override fun open(shader: Shader, glslCode: GlslCode): PaintShader {
            if (glslCode.functionNames.contains("main")) {
                return GenericPaintShader(shader, glslCode)
            } else if (glslCode.functionNames.contains("mainImage")) {
                return ShaderToyPaintShader(shader, glslCode)
            } else
                error("Can't identify paint shader type.")
        }
    },

    Filter(
        4, mapOf(ContentType.ColorStream to ShaderChannel.Main), ContentType.ColorStream, """
        vec4 mainFilter(vec4 inColor) {
            return inColor;
        }
    """.trimIndent()
    ) {
        override fun matches(glslCode: GlslCode): Boolean {
            return glslCode.functionNames.contains("mainFilter")
        }

        override fun open(shader: Shader, glslCode: GlslCode) =
            FilterShader(shader, glslCode)
    };

    abstract fun matches(glslCode: GlslCode): Boolean
    abstract fun open(shader: Shader, glslCode: GlslCode): OpenShader

    fun shaderFromTemplate(): MutableShader {
        return MutableShader("Untitled $name Shader", this, template)
    }
}