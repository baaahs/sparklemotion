// Change colors of Moving Head fixtures. We should add a color selector here.
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

struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo

uniform FixtureInfo fixtureInfo;

uniform float time; // @@Time
uniform bool rotateColor; // @@Switch enabled=true
uniform bool changeColorOnBeat; // @@Switch enabled=true


#define SIMULATE_BPM false

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

float getSeed(){
    return getTimeOfLastBeat() * getBPM() / 60.;
}

// @param inHead moving-head-params
// @param params moving-head-params
void main(in MovingHeadParams inHead, out MovingHeadParams params) {
    params = inHead;

    if(changeColorOnBeat) {
        params.colorWheel =  mod(getSeed(), 10.)/10.;
    } else if(rotateColor){
        params.colorWheel = sin(time/5.);
    }
}
