package baaahs.shows

import baaahs.Model
import baaahs.Show
import baaahs.ShowRunner
import baaahs.shaders.GlslShader

object GlslShow : Show("Glsl Native 55301") {
    val program = """
#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;

#define N 6

void main( void ) {
	vec2 v=(gl_FragCoord.xy-(resolution*0.5))/min(resolution.y,resolution.x)*10.0;
	float t=time * 0.4,r=0.0;
	for (int i=0;i<N;i++){
		float d=(3.14159265 / float(N))*(float(i)*5.0);
		r+=length(vec2(v.x,v.y))+0.01;
		v = vec2(v.x+cos(v.y+cos(r)+d)+cos(t),v.y-sin(v.x+cos(r)+d)+sin(t));
	}
        r = (sin(r*0.1)*0.5)+0.5;
	r = pow(r, 128.0);
	gl_FragColor = vec4(r,pow(max(r-0.75,0.0)*4.0,2.0),pow(max(r-0.875,0.0)*8.0,4.0), 1.0 );
//	gl_FragColor = vec4(gl_FragCoord.x, gl_FragCoord.y, r, 1.0);
}
"""
    
    val program2 = """
uniform float time;
uniform vec2 resolution;

#define PI 3.14159265359

void main(void)
{
	vec2 uv = gl_FragCoord.xy / resolution.xy;
	gl_FragColor = vec4(0);
    gl_FragColor.r = sin(88.*uv.x) + sin(55.*uv.y) + 0.5+2.*sin(time*2.);
}
    """.trimIndent()

    override fun createRenderer(model: Model<*>, showRunner: ShowRunner): Renderer {
        val shader = GlslShader(program)
        showRunner.allSurfaces.map { showRunner.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
            }
        }
    }
}