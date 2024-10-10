uniform float time;

struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo
uniform float ringSpeed; // @@Slider default=10. min=5 max=20
uniform float ringDensity; // @@Slider default=60. min=10 max=120
uniform float ringContrast; // @@Slider default=.5 min=0 max=1

// @param fragCoord uv-coordinate
// @return color
vec4 upstreamColor(vec2 fragCoord);

// @return color
// @param uvIn uv-coordinate
// @param inColor color
vec4 main(vec2 uvIn) {
    vec4 inColor = upstreamColor(uvIn);
    vec2 p = uvIn - .5;
    float directMod = sin(time * 0.01);
    float d = mix(1. - ringContrast, 1., sin(-length(p) * ringDensity * (0.8+ beatInfo.intensity * 0.2) + time * ringSpeed)); // * beatInfo.intensity;
    return vec4(inColor.rgb * d, 1.0);
}