// Music-synced LAZORZZ
// adapted from https://www.shadertoy.com/view/XtBXW3

// WIP WIP WIP This only works in shadertoy but not sparklemotion for some reason....

#define PI 3.1415926535897932384626433832795

uniform float time; // @@Time
//uniform float hueRange; // @@Slider default=0.3 min=0.0 max=1.0

uniform vec2 resolution; // @@Resolution
uniform float speed; // @@Slider default=5.0 min=1.0 max=10.0
uniform vec2 center; // @@XyPad

struct BeatInfo {
	float beat;
	float bpm;
	float intensity;
	float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo

/* Returns a value which cycles from 0 to 1 for each beat, with most of the increase occuring near the beat */
float beatIntegral() {
	float t = mod(beatInfo.beat, 1.);
	float POWER = 4.; // Adjusts sharpnett of the curve
	float OFFSET = 0.0; // Adjusts future-offset of curve. OFFSET=0.5 means the steepest part happens between beats.
	return 1. - pow(1. - mod(t + OFFSET, 1.0), POWER);
}

/* Returns a monotonically increasing time value which with most of the increase occuring near the beat */
float pulsedTime() {
	float timeAdjustment = beatIntegral() - mod(beatInfo.beat, 1.);
	return speed * .2 * 0.87 * (time + .5 * timeAdjustment); // 0.87 keeps it from pausing at the same spot each cycle
}
float t() { return pulsedTime(); }

float laserAmplitude() {
	return 0.5 + 1.0 * beatInfo.intensity;
}

vec3 Strand(in vec2 uv, in vec3 color, in float hoffset, in float hscale, in float vscale, in float timescale)
{
	float glow = 0.06 * resolution.y;
	float curve = 1.0 - abs(uv.y - (
	sin(mod(uv.x * hscale / 100.0 / resolution.x * 1000.0 + t() * timescale + hoffset, 2. * PI))
	* resolution.y * 0.25 * vscale + resolution.y / 2.0
	));
	float i = clamp(curve, 0.0, 1.0);
	i += clamp((glow + curve) / glow, 0.0, 1.0) * 0.4 ;
	return i * color;
}

vec3 Muzzle(in vec2 uv, in float timescale)
{
	float theta = atan(resolution.y / 2.0 - uv.y, resolution.x - uv.x + 0.13 * resolution.x);
	float len = resolution.y * (10.0 + sin(theta * 20.0 + float(int(t() * 20.0)) * -35.0)) / 11.0;
	float d = max(-0.6, 1.0 - (sqrt(
	pow(abs(resolution.x - uv.x), 2.0) + pow(abs(resolution.y / 2.0
	- ((uv.y - resolution.y / 2.0) * 4.0 + resolution.y / 2.0)), 2.0)
	) / len));
	return vec3(
	d * (1.0 + sin(theta * 10.0 + floor(t() * 20.0) * 10.77) * 0.5),
	d * (1.0 + -cos(theta * 8.0 - floor(t() * 20.0) * 8.77) * 0.5),
	d * (1.0 + -sin(theta * 6.0 - floor(t() * 20.0) * 134.77) * 0.5)
	);
}

// @return color
// @param uvIn uv-coordinate
vec4 main(vec2 uvIn) {
	float timescale = 4.0;
	vec3 c = vec3(0, 0, 0);
	c += Strand(uvIn, vec3(1.0, 0, 0), 0.7934 + 1.0 + sin(t()) * 30.0, 1.0, 0.16, 10.0 * timescale);
	c += Strand(uvIn, vec3(0.0, 1.0, 0.0), 0.645 + 1.0 + sin(t()) * 30.0, 1.5, 0.2, 10.3 * timescale);
	c += Strand(uvIn, vec3(0.0, 0.0, 1.0), 0.735 + 1.0 + sin(t()) * 30.0, 1.3, 0.19, 8.0 * timescale);
	c += Strand(uvIn, vec3(1.0, 1.0, 0.0), 0.9245 + 1.0 + sin(t()) * 30.0, 1.6, 0.14, 12.0 * timescale);
	c += Strand(uvIn, vec3(0.0, 1.0, 1.0), 0.7234 + 1.0 + sin(t()) * 30.0, 1.9, 0.23, 14.0 * timescale);
	c += Strand(uvIn, vec3(1.0, 0.0, 1.0), 0.84525 + 1.0 + sin(t()) * 30.0, 1.2, 0.18, 9.0 * timescale);
	c += clamp(Muzzle(uvIn, timescale), 0.0, 1.0);
	return vec4(c.r, c.g, c.b, 1.0);
}


