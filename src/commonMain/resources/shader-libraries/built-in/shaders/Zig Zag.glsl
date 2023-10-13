// Zig Zag

uniform float time;
// uniform float zig; // @type float
uniform float frequency;// @@Slider default=10. min=0 max=20
uniform float depth;// @@Slider default=0.1 min=0.0 max=0.2

struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};
uniform BeatInfo beatInfo;// @@baaahs.BeatLink:BeatInfo

// @return uv-coordinate
// @param uvIn uv-coordinate
vec2 main(vec2 uvIn) {
    vec2 p = -1.0 + 2.0 * uvIn;
    float len = p.x / 16.;
    int y = int(uvIn.y * 10.);

    return vec2(uvIn.x, uvIn.y + sin(uvIn.x * frequency + time * 10.) * depth);
}