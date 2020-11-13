package baaahs.glsl

import baaahs.show.Shader
import baaahs.show.ShaderType

object Shaders {
    val smpteColorBars = Shader("SMTPE Color Bars", ShaderType.Paint, """
        // Robby Kraft
        // from https://www.shadertoy.com/view/XlGXRz

        void mainImage( out vec4 fragColor, in vec2 fragCoord )
        {
            float br = 0.75; // a less popular SMPTE version uses 1.0

        	vec2 st = fragCoord.xy/iResolution.xy;

        	bool sev1 = bool( step(st.x, 1.0/7.0) );
        	bool sev2 = bool( step(st.x, 2.0/7.0) );
        	bool sev3 = bool( step(st.x, 3.0/7.0) );
        	bool sev4 = bool( step(st.x, 4.0/7.0) );
        	bool sev5 = bool( step(st.x, 5.0/7.0) );
        	bool sev6 = bool( step(st.x, 6.0/7.0) );

        	bool row1 = !bool( step(st.y, 0.3333) );
        	bool row2 = !bool( step(st.y, 0.25) );

        	/////////////////////////////////////////
        	// R : 0.75  1.0            1.0  1.0      
        	// G : 0.75  1.0  1.0  1.0                
        	// B : 0.75       1.0       1.0       1.0
        	/////////////////////////////////////////
        	float top_red =   br * float((sev6 && !sev4) || sev2 );
        	float top_green = br * float(sev4);
        	float top_blue =  br * float(!sev6 || (sev5 && !sev4) || (sev3 && !sev2) || sev1 );

        	/////////////////////////////////////////
        	// R :           1.0                 0.75      
        	// G :                     1.0       0.75                
        	// B : 1.0       1.0       1.0       0.75
        	/////////////////////////////////////////
        	float mid_red =   0.075*float( (sev6 && !sev5)||(sev4 && !sev3)||(sev2 && !sev1) ) + br * float(!sev6 || (sev3 && !sev2) );
        	float mid_green = 0.075*float( (sev6 && !sev5)||(sev4 && !sev3)||(sev2 && !sev1) ) + br * float(!sev6 || (sev5 && !sev4) );
        	float mid_blue =  0.075*float( (sev6 && !sev5)||(sev4 && !sev3)||(sev2 && !sev1) ) + br * float(!sev6 || (sev5 && !sev4) || (sev3 && !sev2) || sev1);

        	///////////////////////
        	// R: 0.00  1.0  0.22
        	// G: 0.24  1.0  0.00
        	// B: 0.35  1.0  0.5
        	/////////////////////// 
        	bool fourth1 = bool( step(st.x, 1.0*(5.0/7.0)/4.0 ));
        	bool fourth2 = bool( step(st.x, 2.0*(5.0/7.0)/4.0 ));
        	bool fourth3 = bool( step(st.x, 3.0*(5.0/7.0)/4.0 ));
        	bool fourth4 = bool( step(st.x, (5.0/7.0) ));

        	bool littleThird1 = bool( step(st.x, 5.0/7.0 + 1.0/7.0/3.0) );
        	bool littleThird2 = bool( step(st.x, 5.0/7.0 + 1.0/7.0/3.0*2.0) );
        	bool littleThird3 = bool( step(st.x, 5.0/7.0 + 1.0/7.0/3.0*3.0) );

        	float bottom_red =                         float(fourth2 && !fourth1) + 0.22*float(fourth3 && !fourth2) + 0.075*float(fourth4 && !fourth3) + 0.075*float(littleThird2 && !littleThird1) + 0.15*float(littleThird3 && !littleThird2) + 0.075*float(!sev6);
        	float bottom_green = 0.24*float(fourth1) + float(fourth2 && !fourth1)                                   + 0.075*float(fourth4 && !fourth3) + 0.075*float(littleThird2 && !littleThird1) + 0.15*float(littleThird3 && !littleThird2) + 0.075*float(!sev6);
        	float bottom_blue =  0.35*float(fourth1) + float(fourth2 && !fourth1) + 0.5*float(fourth3 && !fourth2)  + 0.075*float(fourth4 && !fourth3) + 0.075*float(littleThird2 && !littleThird1) + 0.15*float(littleThird3 && !littleThird2) + 0.075*float(!sev6);

        	fragColor = vec4(top_red*float(row1)   + mid_red*float(row2 && !row1) + bottom_red*float(!row2), 
        	                 top_green*float(row1) + mid_green*float(row2 && !row1) + bottom_green*float(!row2), 
        	                 top_blue*float(row1)  + mid_blue*float(row2 && !row1) + bottom_blue*float(!row2),1.);
        }
    """.trimIndent())

    val red = Shader("Solid Red", ShaderType.Paint, """
        void mainImage(out vec4 fragColor, in vec2 fragCoord) {
            fragColor = (1., 0., 0., 1.);
        }
    """.trimIndent())

    val blue = Shader("Solid Blue", ShaderType.Paint, """
        void mainImage(out vec4 fragColor, in vec2 fragCoord) {
            fragColor = (0., 0., 1., 1.);
        }
    """.trimIndent())

    val checkerboard = Shader("Checkerboard", ShaderType.Paint, """
        uniform float checkerboardSize = 10.0; // @@Slider min=.001 max=1 default=.1

        void mainImage(out vec4 fragColor, in vec2 fragCoord) {
            vec2 pos = floor(fragCoord / checkerboardSize);
            vec3 patternMask = vec3(mod(pos.x + mod(pos.y, 2.0), 2.0));
            fragColor = vec4(patternMask.xyz, 1.);
        }
    """.trimIndent())

    val ripple = Shader("Ripple", ShaderType.Distortion, """
        uniform float time;

        vec2 mainDistortion(vec2 uvIn) {
          vec2 p = -1.0 + 2.0 * uvIn;
          float len = length(p);
          return uvIn + (p/len)*cos(len*12.0-time*4.0)*0.03;
        }
    """.trimIndent())

    val flipY = Shader("Flip Y", ShaderType.Distortion, """
        vec2 mainDistortion(vec2 uvIn) {
          return vec2(uvIn.x, 1. - uvIn.y);
        }
    """.trimIndent())

    val cylindricalProjection = Shader("Cylindrical Projection", ShaderType.Projection,
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
            
            vec2 mainProjection(vec2 rasterCoord) {
                int rasterX = int(rasterCoord.x);
                int rasterY = int(rasterCoord.y);
                
                vec3 pixelCoord = texelFetch(pixelCoordsTexture, ivec2(rasterX, rasterY), 0).xyz;
                return project(pixelCoord);
            }
        """.trimIndent()
    )

    val pixelUvIdentity = Shader("Pixel U/V Identity", ShaderType.Paint, """
        void mainImage(out vec4 fragColor, in vec2 fragCoord) {
            fragColor = vec4(fragCoord.x, fragCoord.y, 0., 1.);
        }
    """.trimIndent())
}