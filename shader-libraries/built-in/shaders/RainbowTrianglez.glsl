// Rainbow Trianglez

// Referenced from https://www.shadertoy.com/view/Mtj3Rh

#ifdef GL_ES
precision mediump float;
#endif

#define PI 3.14159265359

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

float random(float n) {
	return fract(abs(sin(n * 55.753) * 367.34));   
}

float random(vec2 n) {
	return random(dot(n, vec2(2.46, -1.21)));
}

float cycle(float n) {
	return cos(fract(n) * 2.0 * PI) * 0.5 + 0.5;
}

//HSB Color to rgb
vec3 hsbToRGB(float h,float s,float b){
	return b*(1.0-s)+(b-b*(1.0-s))*clamp(abs(abs(6.0*(h-vec3(0,1,2)/3.0))-3.0)-1.0,0.0,1.0);
}

void main( void ) {
	vec2 st = (gl_FragCoord.xy * 2.0 - resolution) / min(resolution.x, resolution.y);
	
	float radian = radians(60.0);
	float scale = 2.0;
	
	st = (st + vec2(st.y, 0.0) * cos(radian)) + vec2(floor(4.0 * (st.x - st.y * cos(radian))), 0.0);
	
	st *= scale;
	
 	float n = cycle(random(floor(st * 4.0)) * 0.2 + random(floor(st * 2.0)) * 0.3 + random(floor(st)) * 0.5 + time * 0.125);
	

	vec3 color = hsbToRGB(fract(time*0.05 + random(n*0.00001)), 1.0, 1.0);
	
	gl_FragColor = vec4(color, 1.0);
}
