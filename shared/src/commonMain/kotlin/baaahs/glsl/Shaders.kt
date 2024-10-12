package baaahs.glsl

import baaahs.show.Shader

object Shaders {
    val smpteColorBars = Shader(
        "SMTPE Color Bars",
        /**language=glsl*/
        """
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
        """.trimIndent()
    )

    val earthLikePlanet = Shader(
        "Earth-Like Planet",
        /**language=glsl*/
    """
        #define MAX_STEPS 100
        #define MAX_DIST 100.
        #define SURF_DIST .001
        #define OCEANS_LEVEL .60
        #define PLANETCENTER vec3(-4, -3, 25)
        #define PLANETRADIUS 7.0
        #define MOONCENTER vec3(54, -50, 145)
        #define MOONRADIUS 7.0
        #define ATMOSPHERETHICKNESS .5
        #define ATMOSPHEREDENSITY 2.0
        #define LIGHTPOS vec3(20*3, 15*3, 10)

        const float PI  = 3.14159265359;
        const float PHI = 1.61803398875;

        vec2 sphIntersect( in vec3 ro, in vec3 rd, in vec3 ce, float ra )
        {
            // from Inigo Quilez
            // https://iquilezles.org/articles/intersectors

            vec3 oc = ro - ce;
            float b = dot( oc, rd );
            float c = dot( oc, oc ) - ra*ra;
            float h = b*b - c;
            if( h < 0.0 ) return vec2(-1.0); // no intersection
            h = sqrt( h );
            return vec2( -b-h, -b+h );
        }

        vec2 inverseSF( vec3 p, float n ) 
        {
            // from IQ
            // https://www.shadertoy.com/view/lllXz4

            float m = 1.0 - 1.0/n;
            
            float phi = min(atan(p.y, p.x), PI), cosTheta = p.z;
            
            float k  = max(2.0, floor( log(n * PI * sqrt(5.0) * (1.0 - cosTheta*cosTheta))/ log(PHI+1.0)));
            float Fk = pow(PHI, k)/sqrt(5.0);
            vec2  F  = vec2( round(Fk), round(Fk * PHI) ); // k, k+1

            vec2 ka = 2.0*F/n;
            vec2 kb = 2.0*PI*( fract((F+1.0)*PHI) - (PHI-1.0) );    
            
            mat2 iB = mat2( ka.y, -ka.x, 
                            kb.y, -kb.x ) / (ka.y*kb.x - ka.x*kb.y);
            
            vec2 c = floor( iB * vec2(phi, cosTheta - m));
            float d = 8.0;
            float j = 0.0;
            for( int s=0; s<4; s++ ) 
            {
                vec2 uv = vec2( float(s-2*(s/2)), float(s/2) );
                
                float i = dot(F, uv + c); // all quantities are ingeters (can take a round() for extra safety)
                
                float phi = 2.0*PI*fract(i*PHI);
                float cosTheta = m - 2.0*i/n;
                float sinTheta = sqrt(1.0 - cosTheta*cosTheta);
                
                vec3 q = vec3( cos(phi)*sinTheta, sin(phi)*sinTheta, cosTheta );
                float squaredDistance = dot(q-p, q-p);
                if (squaredDistance < d) 
                {
                    d = squaredDistance;
                    j = i;
                }
            }
            return vec2( j, sqrt(d) );
        }

        float GetHash1(float p)
        {
            // from IQ
            return fract(sin(p)*158.5453123);
        }

        float GetHash(vec3 p)
        {
            // from IQ
        	p  = fract( p*0.3183099+.1 );
        	p *= 17.0;
            return fract( p.x*p.y*p.z*(p.x+p.y+p.z) );
        }

        float GetNoise( in vec3 x )
        {
            vec3 p = floor(x);
            vec3 f = fract(x);
            f = f*f*(3.0-2.0*f);
        	
            return mix(mix(mix( GetHash(p+vec3(0,0,0)), 
                                GetHash(p+vec3(1,0,0)),f.x),
                           mix( GetHash(p+vec3(0,1,0)), 
                                GetHash(p+vec3(1,1,0)),f.x),f.y),
                       mix(mix( GetHash(p+vec3(0,0,1)), 
                                GetHash(p+vec3(1,0,1)),f.x),
                           mix( GetHash(p+vec3(0,1,1)), 
                                GetHash(p+vec3(1,1,1)),f.x),f.y),f.z);
        }

        float GetFBM(vec3 p)
        {
            float noise = 0.0;
            
            // Domain warping
            vec3 warp = vec3(GetNoise(p*0.8+vec3(13.0, 44.0, 15.0)),
                             GetNoise(p*0.8+vec3(43.0, 74.0, 25.0)),
                             GetNoise(p*0.8+vec3(33.0, 14.0, 75.0)));
                             
            warp -= vec3(0.5);
            
            p+= vec3(123.0, 234.0, 55.0);
            p+= warp*0.6;
            
            noise = GetNoise(p) * 1.0 +
        	        GetNoise(p*2.02) * 0.49 + 
        	        GetNoise(p*7.11) * 0.24 + 
        	        GetNoise(p*13.05) * 0.12 + 
        	        GetNoise(p*27.05) * 0.055 + 
        	        GetNoise(p*55.25) * 0.0025+ 
        	        GetNoise(p*96.25) * 0.00125; 

            return noise;
        }

        float GetFBMClouds(vec3 p)
        {
            float noise = 0.0;
            // Domain warping
            vec3 warp = vec3(GetNoise(p*0.8+vec3(13.0, 44.0, 15.0)),
                             GetNoise(p*0.8+vec3(43.0, 74.0, 25.0)),
                             GetNoise(p*0.8+vec3(33.0, 14.0, 75.0)));
                             
            warp -= vec3(0.5);
            
            p+= vec3(123.0, 234.0, 55.0);
            p+= warp*0.2;
            
            noise = GetNoise(p) * 1.0 +
        	        GetNoise(p*5.02) * 0.49 + 
        	        GetNoise(p*11.11) * 0.24 + 
        	        GetNoise(p*23.05) * 0.12 +
        	        GetNoise(p*45.05) * 0.055; 
        	return noise;
        }

        vec3 GetNormal(vec3 p) 
        {
            return normalize(p-PLANETCENTER);
        }

        float GetSceneDistance(vec3 ro, vec3 rd)
        {
            float dO=0.;
         
            vec2 inter = sphIntersect( ro, rd, PLANETCENTER, PLANETRADIUS);
        	if (inter.x>=0.0)
                return inter.x;
            else
                return MAX_DIST;
        }

        vec3 RotateY(vec3 pos, float angle) 
        {
        	return vec3(
                pos.x * cos(angle) - pos.z * sin(angle),
                pos.y,
                pos.x * sin(angle) + pos.z * cos(angle)
            );
        }

        vec3 RotateX(vec3 pos, float angle) 
        {
        	return vec3(
                pos.x,
                pos.y * cos(angle) - pos.z * sin(angle),
                pos.y * sin(angle) + pos.z * cos(angle)
            );
        }

        float GetLight(vec3 p, vec3 n, float height, float waterMask) 
        {
            vec3 lightPos = LIGHTPOS;
            vec3 l = normalize(lightPos-p);

            // Tricky way to compute the normal ... seems acceptable on screen, only one extra height sample to compute
            vec3 pBis =normalize(p+l*0.01 - PLANETCENTER)*0.5*PLANETRADIUS;
            pBis = RotateY(pBis, iTime*0.05);
            float heightBis = GetFBM(pBis) * 0.6;
            float deltaH = heightBis-height;
            
           	n = normalize(n-deltaH*l*40.0*(1.0-waterMask));
            
            float dif = dot(n, l); // can go - (used for night lights)
            float d = GetSceneDistance(p+n*SURF_DIST*2., l);
            if(d<length(lightPos-p)) dif *= .1;
            
            return dif;
        }

        float Density(vec3 pos)
        {
            float distanceToCenter =  length(pos - PLANETCENTER);
            float relativePos = clamp((distanceToCenter-PLANETRADIUS)/ATMOSPHERETHICKNESS, 0.0, 1.0);
            
            return ATMOSPHEREDENSITY* exp(-relativePos);
        }

        float smin( float a, float b)
        {
            float k = 0.03;
            float h = clamp( 0.5+0.5*(b-a)/k, 0.0, 1.0 );
            return mix( b, a, h ) - k*h*(1.0-h);
        }

        vec3 RayTrace(vec3 ro, vec3 rd, vec4 noise) 
        {
        	vec3 color = vec3(0,0,0);
            
            float dist = GetSceneDistance(ro,rd);

            vec2 atmosInOut = sphIntersect( ro, rd, PLANETCENTER, ATMOSPHERETHICKNESS+PLANETRADIUS);
            
            if (dist<MAX_DIST)
            {
                // Hit planet

                atmosInOut.y = dist;
                vec3 pos = ro + rd * dist;   
        		vec3 n = GetNormal(pos);
                
                vec3 localPos = (pos - PLANETCENTER)*0.5;
        		localPos = RotateY(localPos, iTime*0.05);
                float heatDistribution = smoothstep(2.5, 0.5, (abs(localPos.y) + GetNoise(localPos+vec3(-20,55,20))));
                
                float height = GetFBM(localPos) * 0.6;
                float waterMask = smoothstep(OCEANS_LEVEL+0.05, OCEANS_LEVEL-0.05, height);

                float waterHeight = clamp((height / OCEANS_LEVEL),0.0,1.1); 
                float groundHeight = clamp(((height-OCEANS_LEVEL) / (1.0-OCEANS_LEVEL)),0.0,1.0); 
                float foamNoise =  GetNoise(localPos*7.0*(0.75+0.25*sin(iTime*0.1)))*0.25;
                float foamLimit = smoothstep(0.6+foamNoise, 1.0, waterHeight);
                foamLimit = 0.7*foamLimit + 0.3*fract(foamLimit*4.0-iTime*.2)*foamLimit*smoothstep(1.0, 0.85, foamLimit);

        		float iceDistribution = smoothstep(4.0, 4.3, foamLimit + smoothstep(0.7, 0.85, height)*0.25+ (abs(localPos.y) -waterMask*0.5 + height*0.5 + GetNoise(localPos+vec3(-20,55,20))));
        		float wideIceDistribution = smoothstep(2.5, 3.0, foamLimit + smoothstep(0.7, 0.85, height)*0.25+ (abs(localPos.y) -waterMask*0.5 + height*0.5));

                float flatening = waterMask*(1.0-iceDistribution);
                flatening = max(flatening, iceDistribution*0.75);
                
                float light = GetLight(pos, n, height, flatening);
                float dif = clamp(light, 0., 1.);
                float invdif = smoothstep(0.1, 0.1-0.05, light);
                
        		vec3 lightPos = LIGHTPOS;
        	    vec3 l = normalize(lightPos-pos);
        	    float rawNdotL = clamp(dot(n, l), 0.0, 1.0);
                rawNdotL = smoothstep(0.0, 0.2, rawNdotL);
        		dif = max(dif,rawNdotL*0.4); 
                dif*=rawNdotL;
                
                vec3 groundColor = mix (vec3(.3,.1,0), vec3(1.0,.5,.40),clamp(groundHeight*3.0, 0.0, 1.0));
                groundColor = mix (groundColor, vec3(0.76,.36,.36),clamp((groundHeight-0.23)*3.0, 0.0, 1.0));
                groundColor = mix (groundColor, vec3(0.86,.8,.8),clamp((groundHeight-0.53)*6.0, 0.0, 1.0));
                vec3 hotWaterColor =mix (vec3(0.0,.2,.6), vec3(0.0,.4,1.00),smoothstep(0.0, 0.9, waterHeight)); 
                vec3 coldWaterColor =mix (vec3(0.15,.3,0.500), vec3(0.2,0.5,.7), smoothstep(0.0, 0.9, waterHeight)); 
                vec3 waterColor =mix (coldWaterColor, hotWaterColor, heatDistribution); 
        		waterColor = mix (waterColor , vec3(0.7,.8,0.9),foamLimit);

                vec3 finalColor = mix(groundColor, waterColor, waterMask);

        		finalColor = mix (finalColor, mix(vec3(1.1,1.1,1.1),vec3(.9,1.0,1.0),abs(sin(height*10.0))), iceDistribution);

                float fresnel =pow (1.0-clamp(dot(n, -rd), 0., 1.), 3.0);

                invdif*= (1.0-fresnel);
                dif *= (1.0-fresnel);
                color = vec3(dif) * finalColor;
                
        		float citiesLimit = smoothstep(0.0, 0.1, groundHeight)*smoothstep(0.15, 0.13, groundHeight)*(1.0-waterMask)*(1.0-iceDistribution);

                float greenArea = smoothstep(0.0, 0.01, groundHeight)*smoothstep(0.45, 0.3, groundHeight)*(1.0-waterMask)*(1.0-wideIceDistribution);

                vec2 cityInfo = inverseSF(normalize(localPos), 150.0)+
                    inverseSF(normalize(localPos), 100.0);
                localPos = normalize(localPos);
                
                vec2 lightInfo1 = inverseSF(localPos, 23000.0);
                vec2 lightInfo2 = inverseSF(RotateX(localPos, 1.0), 38000.0);
                vec2 lightInfo3 = inverseSF(RotateX(localPos, 1.5), 33000.0);

                float townMask = smoothstep(0.28, 0.1, cityInfo.y);
                
        		float lightIntensity = min(min(lightInfo1.y*1.25*(1.0+smoothstep(0.8,0.4, GetHash1((lightInfo1.x)*.015))), 
        								lightInfo2.y*1.75*(1.0+smoothstep(0.8,0.4, GetHash1((lightInfo1.x)*.015)))
                                        ), lightInfo3.y*(1.0+smoothstep(0.8,0.4,GetHash1((lightInfo1.x)*.015))));
        		lightIntensity = smoothstep(0.01, 0.000, lightIntensity);
                
                float cityArea = smoothstep(0.2, 0.18, cityInfo.y) * citiesLimit;
        		citiesLimit*=townMask* lightIntensity;
        	        
                greenArea *=smoothstep(0.4, 0.3, cityInfo.y)*smoothstep(0.25, 0.28, cityInfo.y);
                color = mix (color,vec3(dif) * mix(vec3(.45, .65, .15),vec3(.1, .35, .1), GetNoise(localPos*150.0))
                             , clamp(greenArea-GetNoise(localPos*70.0)*0.6, 0.0, 1.0));
                
                color = mix (color, vec3(.15, .15, .15), cityArea);
                color = mix(color, vec3(.35, .35, .35), clamp(citiesLimit*2.0, 0.0, 1.0));
            	color += vec3(0.95, 0.8, 0.5)*invdif*citiesLimit*2.0;
          
        		vec3 localPosClouds = (pos - PLANETCENTER)*0.25;
        		localPosClouds = RotateY(localPosClouds, iTime*0.055 + localPosClouds.y*0.85);
                float clouds = GetFBMClouds(localPosClouds*3.0-vec3(iTime*0.025)) * 0.6;

        		vec3 localPosCloudsBis = (pos+l*0.1 - PLANETCENTER)*0.25;
        		localPosCloudsBis = RotateY(localPosCloudsBis, iTime*0.055);
                float cloudsBis = GetFBMClouds(localPosCloudsBis*3.0-vec3(iTime*0.025)) * 0.6;

                float level = smoothstep(OCEANS_LEVEL-0.1, OCEANS_LEVEL+0.6, height);
                clouds = smoothstep(0.5+level*0.85, 0.8+level*0.85, clouds);
                float cloudShadow = 1.0-smoothstep(0.5+level*0.85, 1.0+level*0.85, cloudsBis)*0.75*(1.0 - clamp(dot(n, l), 0.0, 1.0));
                
                color = mix (color*cloudShadow, vec3(1)*rawNdotL*cloudShadow, clouds);
            }
            else
            {
                
        		float dO=0.;
         
        	    vec2 inter = sphIntersect( ro, rd, MOONCENTER, MOONRADIUS);
        		if (inter.x>=0.0)
                {
                    // moon hit
                    
                    vec3 hitPos = inter.x * rd + ro;
                    vec3 hitNormal = normalize(hitPos-MOONCENTER);
               		vec3 lightPos = LIGHTPOS;
        		    vec3 l = normalize(lightPos-hitPos);

                    float dif = clamp(dot(l, hitNormal), 0.0, 1.0);
                    float noise = GetFBM(hitPos*0.4);
                    color = mix(vec3(0.2), vec3(.4), noise)*dif;
                }
        		else
                {
                    // background
                    
                    vec3 pos = normalize(ro + rd * 1000.0);   
                    pos = pos.xyz;

                    for(float i=0.0;i<5.0;i++)
                    {
                        vec2 info = inverseSF(pos, 50000.0 + i*5000.0);
                        float random = GetHash1((info.x + i*10.0)*.015);
                        float distToStar = smoothstep(0.00025+0.0015*pow((1.0-random), 15.0), 0.0002,info.y) * smoothstep(0.1, 0.0,random ); 

                        color = max(color, vec3(distToStar));
                    }
                        
                    float noise = GetFBM(pos*2.0 + vec3(0.0, 0.0, iTime*0.05));
                    float nebulae = smoothstep(0.4, 1.8, noise);
        			float nebulae1 = max(0.3-abs(nebulae-0.3), 0.0)*1.0;
        			float nebulae2 = max(0.2-abs(nebulae-0.4), 0.0)*1.0;
        			float nebulae3 = max(0.3-abs(nebulae-0.5), 0.0)*1.0;

                    vec3 nebulaeColor = vec3(0.0, 0.2, 0.7)*nebulae1 + vec3(0.5, 0.4, 0.3)*nebulae2 + vec3(0.1, 0.2, 0.4)*nebulae3;

                    color += nebulaeColor;
                }        
            }
            
            if (atmosInOut.x>=0.0)
            {
                float density = .1;
                
                atmosInOut.x-=noise.r;
                // Atmosphere
                float atmosphereToGo = atmosInOut.y - atmosInOut.x;
                
                vec3 startPosition = ro + rd * atmosInOut.x;
                
                float lightEnergy  = 0.0;
                float transmittance = 1.0;
                float steps = 0.0051;
                for(float i = 0.0; i<atmosphereToGo;i+=steps)
                {
        			vec3 pos = startPosition+rd*i;
                    
                    float localDensity = Density(pos)*steps;
                    vec3 lightDir = -normalize(pos-LIGHTPOS);
                    float shadow = GetSceneDistance(pos, lightDir);
                    shadow = step(MAX_DIST*0.9, shadow);
                    
                    lightEnergy+=  localDensity * shadow;
        			transmittance *= (1.0-localDensity*1.0);
                    
                }
                color = mix(color, 
                            mix( vec3(0.1,0.15,0.7), vec3(0.65,0.8,1.0),(1.0-exp(-lightEnergy*0.6)))
                                *clamp(lightEnergy*0.5, 0.0, 1.0),(1.0-transmittance)* (1.0-exp(-lightEnergy*0.6)));
            }
            

            
            return color;
        }

        void mainImage( out vec4 fragColor, in vec2 fragCoord )
        {
            vec2 uv = (fragCoord-.5*iResolution.xy)/iResolution.y * 1.25;
            vec4 noise = texture(iChannel0, fragCoord/1024.0); 
            vec3 offset =  vec3(sin(iTime*0.1)*3.0, 0.25+(cos(iTime*0.1)*.5), 0);
            offset = mix(offset, vec3(4.0-iMouse.x/iResolution.x*8.0, 4.0-(iMouse.y/iResolution.y)*8.0, 0), step( 0.001, iMouse.z ));
            offset = vec3(0.);

            vec3 ro = offset + vec3(-4.0, 11.0, 0.0);
            vec3 rd = normalize(vec3(uv.x, uv.y, 2));
            rd = RotateX(rd, 0.5);
            vec3 col = RayTrace(ro, rd, noise);

            fragColor = vec4(col,1.0);
        }
    """.trimIndent()
    )
    val red = Shader(
        "Solid Red",
        /**language=glsl*/
        """
            void mainImage(out vec4 fragColor, in vec2 fragCoord) {
                fragColor = (1., 0., 0., 1.);
            }
        """.trimIndent()
    )

    val blue = Shader(
        "Solid Blue",
        /**language=glsl*/
        """
            void mainImage(out vec4 fragColor, in vec2 fragCoord) {
                fragColor = (0., 0., 1., 1.);
            }
        """.trimIndent()
    )

    val checkerboard = Shader(
        "Checkerboard",
        /**language=glsl*/
        """
            uniform float checkerboardSize = 10.0; // @@Slider min=.001 max=1 default=.1
    
            void mainImage(out vec4 fragColor, in vec2 fragCoord) {
                vec2 pos = floor(fragCoord / checkerboardSize);
                vec3 patternMask = vec3(mod(pos.x + mod(pos.y, 2.0), 2.0));
                fragColor = vec4(patternMask.xyz, 1.);
            }
        """.trimIndent()
    )

    val ripple = Shader(
        "Ripple",
        /**language=glsl*/
        """
            uniform float time;
            uniform float rippleAmount; // @type float
    
            // @return uv-coordinate
            // @param uvIn uv-coordinate
            vec2 main(vec2 uvIn) {
              vec2 p = -1.0 + 2.0 * uvIn;
              float len = length(p);
              return uvIn + (p/len)*sin(len*12.0-time*4.0)*0.1 * rippleAmount;
            }
        """.trimIndent()
    )

    val flipY = Shader(
        "Flip Y",
        /**language=glsl*/
        """
            // Flip Y
            
            // @return uv-coordinate
            // @param uvIn uv-coordinate
            vec2 main(vec2 uvIn) {
              return vec2(uvIn.x, 1. - uvIn.y);
            }
        """.trimIndent()
    )

    val cylindricalProjection = Shader(
        "Cylindrical Projection",
        /**language=glsl*/
        """
            // Cylindrical Projection
            // !SparkleMotion:internal

            struct ModelInfo {
                vec3 center;
                vec3 extents;
            };
            uniform ModelInfo modelInfo;

            const float PI = 3.141592654;

            // @return uv-coordinate
            // @param pixelLocation xyz-coordinate
            vec2 main(vec3 pixelLocation) {
                vec3 pixelOffset = pixelLocation - modelInfo.center;
                vec3 normalDelta = normalize(pixelOffset);
                float theta = atan(abs(normalDelta.z), normalDelta.x); // theta in range [-π,π]
                if (theta < 0.0) theta += (2.0f * PI);                 // theta in range [0,2π)
                float u = theta / (2.0f * PI) * 2.;                    // u in range [0,1)
                float v = (pixelOffset.y + modelInfo.extents.y / 2.0f) / modelInfo.extents.y;
                return vec2(u, v);
            }
        """.trimIndent()
    )

    val xyProjection = Shader(
        "XY Projection",
        /**language=glsl*/
        """
            // XY Projection

            struct ModelInfo {
                vec3 center;
                vec3 extents;
            };
            uniform ModelInfo modelInfo;

            // @return uv-coordinate
            // @param pixelLocation xyz-coordinate
            vec2 main(vec3 pixelLocation) {
                vec3 pixelOffset = (pixelLocation - modelInfo.center) / modelInfo.extents + .5;
                return vec2(1.-pixelOffset.x, pixelOffset.y);
            }
        """.trimIndent()
    )

    val pixelUvIdentity = Shader(
        "Pixel U/V Identity",
        /**language=glsl*/
        """
            void mainImage(out vec4 fragColor, in vec2 fragCoord) {
                fragColor = vec4(fragCoord.x, fragCoord.y, 0., 1.);
            }
        """.trimIndent()
    )
}