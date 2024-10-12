uniform float soundBrightness; // @@Slider min=0. max=2. default=0.5

struct SoundAnalysis {
    int bucketCount;
    int sampleHistoryCount;
    sampler2D buckets;
    float maxMagnitude;
};
uniform SoundAnalysis soundAnalysis; // @@baaahs.SoundAnalysis:SoundAnalysis

// @return color
// @param inColor color
vec4 main(vec4 inColor) {
    vec4 clampedColor = clamp(inColor, 0., 1.);
    return vec4(clampedColor.rgb * mix(soundAnalysis.maxMagnitude, 1., soundBrightness) , clampedColor.a);
}
