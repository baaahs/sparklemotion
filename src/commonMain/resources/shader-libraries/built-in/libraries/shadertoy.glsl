// Compatibility functions for testing things in ShaderToy

#define time iTime
#define resolution iResolution

uniform float time; // @@Time

struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo


/* Gets the current beat (0,1,2,3) of the song, assuming 4/4 time */
int getCurrentBeat() {
    return int(beatInfo.beat) % 4;
}

/* Returns a float [0,1] which peaks at each beat. Can be used instead of BeatInfo.intensity */
float getFakeIntensity() {
    float t = mod(beatInfo.beat, 1.);
    return smoothstep(1., 0., t / 0.25) + smoothstep(1., 0., (1. - t) / 0.1);
}

/* Returns a value which cycles from 0 to 1 for each beat, with most of the increase occuring near the beat */
float beatIntegral() {
    float t = mod(beatInfo.beat, 1.);
    float POWER = 6.; // Adjusts sharpnett of the curve
    float OFFSET = 0.05; // Adjusts future-offset of curve. OFFSET=0.5 means the steepest part happens between beats.
    return 1. - pow(1. - mod(t + OFFSET, 1.0), POWER);
}