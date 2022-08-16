// Square pulse thingy optimized for BAAAHS
// originally based on https://www.shadertoy.com/view/XsBfRW

uniform float time; // @@Time
uniform vec2 resolution; // @@Resolution
uniform float speed; // @@Slider default=3.0 min=1.0 max=5.0


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
	return speed * 0.87 * (time + .4 * timeAdjustment); // 0.87 keeps it from pausing at the same spot each cycle
}

void mainImage( out vec4 fragColor, in vec2 fragCoord ) {

	float t = pulsedTime();

	float aspect = resolution.y/resolution.x;
	float value;
	vec2 uv = fragCoord.xy / iResolution.x;
	uv -= vec2(0.5, 0.5 * aspect);
	uv *= .6;

	float rot = radians(45.0); // radians(45.0*sin(t));
	mat2 m = mat2(cos(rot), -sin(rot), sin(rot), cos(rot));

	uv = m * uv;
	uv += vec2(0.5, 0.5 * aspect);
	uv.y+=0.5*(1.0 - aspect);
	vec2 pos = 10.0 * uv;
	vec2 rep = fract(pos);
	float dist = 2.0*min(min(rep.x, 1.0-rep.x), min(rep.y, 1.0-rep.y));
	float squareDist = length((floor(pos)+vec2(0.5)) - vec2(5.0) );

	float edge = sin(t-squareDist*0.5)*0.5+0.5;

	edge = (t-squareDist*0.5)*0.5;
	edge = 2.0*fract(edge*0.5);

	value = fract (dist*2.0);
	value = mix(value, 1.0-value, step(1.0, edge));

	edge = pow(abs(1.0-edge), 2.0);

	value = smoothstep( edge-0.05, edge, 0.95*value);


	value += squareDist*.1;

	vec4 baseColor = vec4(0.5 + 0.5*cos(t+uv.xyx+vec3(0,2,4)), 1.0);
	fragColor = mix(vec4(1.0,1.0,1.0,1.0), baseColor, value);
	fragColor.a = 0.25*clamp(value, 0.0, 1.0);
}

