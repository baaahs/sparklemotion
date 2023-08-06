// Filter for Moving Heads to adjust the dimmer to the beat.
// by Aravindo Wingeier

struct FixtureInfo {
    vec3 position;
    vec3 rotation;
    mat4 transformation;
};

struct MovingHeadParams {
    float pan;
    float tilt;
    float colorWheel;
    float dimmer;
};

uniform FixtureInfo fixtureInfo;


uniform float time; // @@Time
#define PI 3.14159265358979323846
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo
#define SIMULATE_BPM false
uniform bool lightBeat; // @@Switch enabled=true

float getBPM() {
    if (SIMULATE_BPM)  {
        return 120.;
    } else {
        return beatInfo.bpm;
    }
}


float getTimeOfLastBeat() {
    // SIM:
    if(SIMULATE_BPM){
        float bpm =  getBPM();
        float beatDuration = 60. / bpm;
        float timeSince2Beats = mod(time * beatDuration, beatDuration );
        return time - timeSince2Beats;
    } else {
        // real:
        float beatDuration = 60. / getBPM();
        float timeSince2Beats = mod(beatInfo.beat, 1.) * beatDuration;
        return time - timeSince2Beats;
    }
}

/* Test function for ShaderToy */
float beatIntensity() {
    if(SIMULATE_BPM){
        float bpm = getBPM();
        float t = mod(time * bpm / 60., 1.);
        return smoothstep(1., 0., t / 0.25) + smoothstep(1., 0., (1. - t) / 0.1);
    } else {
        return beatInfo.intensity;
    }
}


// @param inHead moving-head-params
// @param outHead moving-head-params
void main(in MovingHeadParams inHead, out MovingHeadParams outHead) {
    outHead = inHead;

    if(lightBeat){
        outHead.dimmer = beatIntensity();
    }


}
