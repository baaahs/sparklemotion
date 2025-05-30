// [🐑] ColorPulse
// Color pulses sweeping from front to back of the sheep synchronized to the beat
// @author Ben Bartlett

uniform float time; // @@Time
uniform float hueRange; // @@Slider default=0.3 min=0.0 max=1.0
uniform float pulseSpeed; // @@Slider default=1.0 min=1.0 max=3.0
uniform bool fromCenter; // @@Switch
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

vec3 hsl2rgb(in vec3 c) {
	vec3 rgb = clamp( abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),6.0)-3.0)-1.0, 0.0, 1.0 );
	return c.z + c.y * (rgb-0.5)*(1.0-abs(2.0*c.z-1.0));
}

vec3 hsv2rgb(vec3 c){
	vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
	vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
	return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

// A pulse from 0 to 1 back to 0 with adjustable ramp and width
float smoothstepPulse(float t) {
	float riseTime = 0.3;
	float peakTime = 0.2;
	return smoothstep(0., 1., t / riseTime) - smoothstep(0., 1., (t - peakTime) / riseTime);
}

// @return color
// @param uvIn uv-coordinate
vec4 main(vec2 uvIn) {

	float s = uvIn.x;
	if (fromCenter) {
		s = distance(uvIn, center);
	}

	float pulse = smoothstepPulse(mod(beatIntegral() - s / (2.5*pulseSpeed), 1.));

	float H = .2 * time + s * hueRange;
	float S = .5 + .5 * pulse;
	float V = .2 + .6 * pulse;

	return vec4(hsl2rgb(vec3(H,S,V)), 1.);
}
