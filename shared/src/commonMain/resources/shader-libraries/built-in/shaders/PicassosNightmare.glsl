// Picasso's Nightmare
// From http://glslsandbox.com/e#56499

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;
const float n_circles = 800.0;
const float max_radius = 1.0;
const float min_radius = 0.5;
const float seed1 = 69.7;
const float seed2 = 82.4;
const float seed3 = 62.5;

float rand(float x, float seed) {
    return fract(sin(x) * seed);
}

float randFromTo(float x, float seed, float min, float max){
	return rand(x, seed) * (max - min) + min;
}

bool drawCircle(vec2 pos, vec2 center, float radius, inout vec3 color) {
	vec2 translatedCenter = vec2(sin(time) * randFromTo(center.x, seed1, -1.8, 1.8) + center.x, sin(time) * randFromTo(center.y, seed1, -1., 1.) + center.y);
	float distance = distance(pos, translatedCenter);
	bool ret =  distance <= radius;
	if (ret) {
		float m = max (0.2, smoothstep(radius, radius / 3., distance));
		color = vec3(rand(center.x, seed1) * m,rand(center.y, seed2) * m, rand(center.x + center.y, seed3) * m);
	}
	return ret;
}

void drawCircles(vec2 pos, float radiusMultiplier, inout vec3 color) {
	for (float i=0.; i<n_circles; i++){
		if (drawCircle(pos, vec2(randFromTo(i, seed1, -1.8, 1.8), randFromTo(i, seed2, -1., 1.)), randFromTo(i, seed3, min_radius, max_radius) * radiusMultiplier, color)) {
			break;
		}
	}
}

void main( void ) {
	float minRes = min(resolution.x, resolution.y);
	vec2 pos = (gl_FragCoord.xy * 2. - resolution) / minRes;
	float radiusMultiplier = abs(sin(time + 4. * (pos.x * pos.y)));
	vec3 color = vec3(pos.y, pos.y, pos.y);
	drawCircles(pos, radiusMultiplier, color);
	gl_FragColor = vec4(color, 1);
}
