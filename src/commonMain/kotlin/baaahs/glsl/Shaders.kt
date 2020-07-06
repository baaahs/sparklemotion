package baaahs.glsl

import baaahs.glshaders.GlslAnalyzer

object Shaders {

    val cylindricalUvMapper = GlslAnalyzer().asShader(
        /**language=glsl*/
        """
                // Cylindrical Projection
                // !SparkleMotion:internal
                
                uniform sampler2D pixelCoordsTexture;
                
                struct ModelInfo {
                    vec3 center;
                    vec3 extents;
                };
                uniform ModelInfo modelInfo;

                const float PI = 3.141592654;

                vec2 project(vec3 pixelLocation) {
                    vec3 pixelOffset = pixelLocation - modelInfo.center;
                    vec3 normalDelta = normalize(pixelOffset);
                    float theta = atan(abs(normalDelta.z), normalDelta.x); // theta in range [-π,π]
                    if (theta < 0.0) theta += (2.0f * PI);                 // theta in range [0,2π)
                    float u = theta / (2.0f * PI);                         // u in range [0,1)
                    float v = (pixelOffset.y + modelInfo.extents.y / 2.0f) / modelInfo.extents.y;
                    return vec2(u, v);
                }
                
                vec2 mainUvFromRaster(vec2 rasterCoord) {
                    int rasterX = int(rasterCoord.x);
                    int rasterY = int(rasterCoord.y);
                    
                    vec3 pixelCoord = texelFetch(pixelCoordsTexture, ivec2(rasterX, rasterY), 0).xyz;
                    return project(pixelCoord);
                }
            """.trimIndent()
    )

}