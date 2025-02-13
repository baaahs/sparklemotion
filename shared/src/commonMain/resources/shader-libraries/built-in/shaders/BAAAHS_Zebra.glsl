// Zebra
// Cool zebra pattern looking thing that alternates polarity on the beat
// Ben Bartlett

uniform float time; // @@Time
uniform float speed; // @@Slider min=0.0 max=10.0 default=5.0

struct BeatInfo {
	float beat;
	float bpm;
	float intensity;
	float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo

// @return color
// @param uv uv-coordinate
vec4 main(vec2 uv) {
	float t = time * .01 * speed / 5.0;
	vec2 center = vec2(0.,0.);

	float periods = 4.1;
	center.y+=sin(uv.y*periods + t * 51.);
	center.x+=cos(uv.x*periods + t);

	float slantyness = 5.0;
	center.y+=sin(slantyness*(uv.x + uv.y));
	center.x+=sin(slantyness*(uv.x + uv.y));

	float d = distance(uv,center);
	float kFreq = 2.5;

	float k = -sin(d*6.283*kFreq - t);
	int beat = int(beatInfo.beat);
	if (beat % 2 == 0) {
		k *= -1.;
	}

	float e = smoothstep(0., fwidth(k)*1.5, k);
	return vec4(sqrt(max(e, 0.)));
}