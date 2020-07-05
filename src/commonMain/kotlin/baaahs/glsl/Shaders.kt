package baaahs.glsl

import baaahs.glshaders.GlslAnalyzer

object Shaders {

    val cylindricalUvMapper = GlslAnalyzer().asShader(
        /**language=glsl*/
        """
                // Cylindrical Projection
                // !SparkleMotion:internal
                
                uniform sampler2D uvCoordsTexture;
                
                vec2 mainUvFromRaster(vec2 rasterCoord) {
                    int rasterX = int(rasterCoord.x);
                    int rasterY = int(rasterCoord.y);
                    
                    vec2 uvCoord = vec2(
                        texelFetch(uvCoordsTexture, ivec2(rasterX * 2, rasterY), 0).r,    // u
                        texelFetch(uvCoordsTexture, ivec2(rasterX * 2 + 1, rasterY), 0).r // v
                    );
                    return uvCoord;
                }
            """.trimIndent()
    )

}