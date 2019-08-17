package baaahs.shows

object GlslRedBeatShow : GlslShow("GlslRedBeatShow") {

    override val program = """

// SPARKLEMOTION GADGET: MeasureStartTimeMs {name: "measureStartTime" }
//uniform float measureStartTimeMs;

// SPARKLEMOTION GADGET: Beat {name: "beat" }
uniform float beat;

vec4 color(float marker) {
	float r = (1.0 + sin(beat  + 1.0)) / 2.0;
	float g = (1.0 + sin(beat  + 2.0)) / 2.0;
	float b = (1.0 + sin(beat  + 4.0)) / 2.0;
	
	return vec4(r, g, b, 1.0);
}

#define ITERATIONS                                                          70
#define SCALING .0002
void main() {
    gl_FragColor = vec4(beat, 0., 0., 1.);
}
    """.trimIndent()
}
