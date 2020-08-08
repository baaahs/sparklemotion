package baaahs.show

import baaahs.glshaders.ContentType
import baaahs.glshaders.GlslCode
import baaahs.show.mutable.MutableShader

enum class ShaderType(
    val priority: Int,
    val defaultUpstreams: Map<ContentType, ShaderChannel>,
    val resultContentType: ContentType,
    /**language=glsl*/
    val template: String
) {
    Projection(0, emptyMap(), ContentType.UvCoordinate, """
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
            
            vec2 mainUvFromRaster(vec2 rasterCoord) {
                int rasterX = int(rasterCoord.x);
                int rasterY = int(rasterCoord.y);
                
                vec3 pixelCoord = texelFetch(pixelCoordsTexture, ivec2(rasterX, rasterY), 0).xyz;
                return project(pixelCoord);
            }
    """.trimIndent()) {
        override fun matches(glslCode: GlslCode): Boolean {
            return glslCode.functionNames.contains("mainUvFromRaster")
        }
    },

    Distortion(1, mapOf(ContentType.UvCoordinate to ShaderChannel.Main), ContentType.UvCoordinate, """
        // ... TODO
    """.trimIndent()) {
        override fun matches(glslCode: GlslCode): Boolean {
            return false // TODO
        }
    },

    Paint(0, mapOf(ContentType.UvCoordinate to ShaderChannel.Main), ContentType.Color, """
        uniform vec2 resolution;
        uniform float time;

        void main(void) {
            vec2 position = gl_FragCoord.xy / resolution.xy;
            gl_FragColor = vec4(position.xy, mod(time, 1.), 1.);
        }
    """.trimIndent()) {
        override fun matches(glslCode: GlslCode): Boolean {
            return glslCode.functionNames.contains("main") ||
                    glslCode.functionNames.contains("mainImage")
        }
    },

    Filter(1, mapOf(ContentType.Color to ShaderChannel.Main), ContentType.Color, """
        vec4 filterImage(vec4 inColor) {
            return inColor;
        }
    """.trimIndent()) {
        override fun matches(glslCode: GlslCode): Boolean {
            return glslCode.functionNames.contains("filterImage")
        }
    };

    abstract fun matches(glslCode: GlslCode): Boolean

    fun shaderFromTemplate(): MutableShader {
        return MutableShader("Untitled ${name} Shader", this, template)
    }
}