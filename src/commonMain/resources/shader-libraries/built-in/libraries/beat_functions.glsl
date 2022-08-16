// Some beat-related functions I use in my shaders
// Ben Bartlett

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

/* Test function for ShaderToy */
float getFakeIntensity_shadertoy() {
    float bpm = 120.;
    float t = mod(iTime * bpm / 60., 1.);
    return smoothstep(1., 0., t / 0.25) + smoothstep(1., 0., (1. - t) / 0.1);
}

/* Returns a value which cycles from 0 to 1 for each beat, with most of the increase occuring near the beat */
float beatIntegral() {
    float t = mod(beatInfo.beat, 1.);
    float POWER = 4.; // Adjusts sharpness of the curve
    float OFFSET = 0.0; // Adjusts future-offset of curve. OFFSET=0.5 means the steepest part happens between beats.
    return 1. - pow(1. - mod(t + OFFSET, 1.0), POWER);
}

/* Returns a value which cycles from 0 to 1 over a beat, quickly but smoothly returning to 0 at the end */
float beatIntegral_smooth() {
    float t = mod(beatInfo.beat, 1.);
    float POWER = 4.; // Adjusts sharpness of the curve
    float OFFSET = 0.0; // Adjusts future-offset of curve. OFFSET=0.5 means the steepest part happens between beats.
    float RESET_PERIOD = 0.1; // The last part of the beat where the value resets
    float adjustedTime = mod(t + OFFSET, 1.0);
    if (adjustedTime > (1. - RESET_PERIOD)) {
        return smoothstep(1., 0., (adjustedTime - (1. - RESET_PERIOD)) / RESET_PERIOD);
    } else {
        return 1. - pow(1. - adjustedTime / (1. - RESET_PERIOD), POWER);
    }
}

/* Test function for ShaderToy */
float beatIntegral_shadertoy() {
    float bpm = 120.;
    float t = mod(iTime * bpm / 60., 1.);
    float POWER = 4.; // Adjusts sharpnett of the curve
    float OFFSET = 0.05; // Adjusts future-offset of curve. OFFSET=0.5 means the steepest part happens between beats.
    return 1. - pow(1. - mod(t + OFFSET, 1.0), POWER);
}

/* Returns a monotonically increasing time value which with most of the increase occuring near the beat */
float pulsedTime() {
    float timeAdjustment = beatIntegral() - mod(beatInfo.beat, 1.);
    return time + timeAdjustment;
}

