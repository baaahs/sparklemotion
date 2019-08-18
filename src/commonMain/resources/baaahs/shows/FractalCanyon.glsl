// Fractal Canyon
// From http://glslsandbox.com/e#56688

//Vlad's fractal canyon. 11/17/2016

precision mediump float;

uniform float time;
uniform vec2 resolution;
//varying vec2 surfacePosition;

#define FREQ 0.2
vec4 color(float marker) {
	float r = (1.0 + sin(FREQ * marker + 1.0)) / 2.0;
	float g = (1.0 + sin(FREQ * marker + 2.0)) / 2.0;
	float b = (1.0 + sin(FREQ * marker + 4.0)) / 2.0;
	
	return vec4(r, g, b, 1.0);
}

#define ITERATIONS                                                          70
#define SCALING .0002
void main() {
	vec2 coord = vec2(gl_FragCoord.xy);
	vec2 z, z0, zT;

	z0.x = coord.x / (SCALING * time) / (time * 20.0) - (0.702985 + (time * 0.00000005));
	z0.y = coord.y / (SCALING * time) / (time * 20.0) - (.299 + (time * 0.00000005));

	float F;
	for(int i = 0; i < ITERATIONS; i++) {
		if(dot(z,z) > 16.0) break;
		zT.x = (z.x * z.x - z.y * z.y) + z0.x;
		zT.y = (z.y * z.x + z.x * z.y) + z0.y;
		z = zT;
		F++;
	}
	gl_FragColor = (F == float(ITERATIONS)) ? vec4(0.0, 0.0, 0.0, 1) : color(.5*time*F - log2(log2(dot(sin(z),(z)))));
}
