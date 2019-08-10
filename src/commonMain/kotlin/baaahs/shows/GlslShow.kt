package baaahs.shows

import baaahs.Gadget
import baaahs.Model
import baaahs.Show
import baaahs.ShowRunner
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.shaders.GlslShader

object GlslShow : Show("GlslSandbox 55301 (OpenGL)") {
    val program = """
#ifdef GL_ES
precision mediump float;
#endif

// SPARKLEMOTION GADGET: Slider {name: "Scale", initialValue: 10.0, minValue: 0.0, maxValue: 100.0}
uniform float scale;

uniform float time;
uniform vec2 resolution;

#define N 6

void main( void ) {
	vec2 v= (gl_FragCoord.xy-(resolution*0.5))/min(resolution.y,resolution.x)*scale;
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

    val gadgetPattern = Regex(
        "\\s*//\\s*SPARKLEMOTION GADGET:\\s*([^\\s]+)\\s+(\\{.*})\\s*\n" +
                "\\s*uniform\\s+([^\\s]+)\\s+([^\\s]+);"
    )

    override fun createRenderer(model: Model<*>, showRunner: ShowRunner): Renderer {
        val shader = GlslShader(program)

        val adjustableValuesToGadgets = shader.adjustableValues.associateWith { it.createGadget(showRunner) }
        val buffers = showRunner.allSurfaces.map { showRunner.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
                buffers.forEach { buffer ->
                    adjustableValuesToGadgets.forEach { (adjustableValue, gadget) ->
                        val value: Any = when (gadget) {
                            is Slider -> gadget.value
                            is ColorPicker -> gadget.color
                            else -> throw IllegalArgumentException("unsupported gadget $gadget")
                        }
                        buffer.update(adjustableValue, value)
                    }
                }
            }
        }
    }

    fun GlslShader.AdjustableValue.createGadget(showRunner: ShowRunner): Gadget {
        val config = config
        val name = config.getPrimitive("name").contentOrNull ?: varName

        val gadget = when (gadgetType) {
            "Slider" -> {
                Slider(
                    name,
                    initialValue = config.getPrimitive("initialValue").floatOrNull ?: 1f,
                    minValue = config.getPrimitive("minValue").floatOrNull,
                    maxValue = config.getPrimitive("maxValue").floatOrNull
                )
            }
            "ColorPicker" -> {
                ColorPicker(name)
            }
            else -> throw IllegalArgumentException("unknown gadget ${gadgetType}")
        }

        return showRunner.getGadget(name, gadget)
    }

}