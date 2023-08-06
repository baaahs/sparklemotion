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
    float beatShift = alternateEye && isLeft() ? 1.0 : 0.;
    float eyesInSequence = alternateEye ? 2. : 1.;

    // SIM:
    if(SIMULATE_BPM){
        float bpm =  getBPM();
        float beatDuration = 60. / bpm;
        float timeSince2Beats = mod(time + beatShift * beatDuration, beatDuration  * eyesInSequence);
        return time - timeSince2Beats;
    } else {
        // real:
        float beatDuration = 60. / getBPM();
        float timeSince2Beats = mod(beatInfo.beat + beatShift, eyesInSequence) * beatDuration;
        return time - timeSince2Beats;
    }
}


// @param inHead moving-head-params
// @param outHead moving-head-params
void main(in MovingHeadParams inHead, out MovingHeadParams outHead) {
    outHead = inHead;

    if(rotateColor){
        outHead.colorWheel = sin(time/5.);
    }

    if(changeColorOnBeat) {
        // TODO: make this work
        params.colorWheel =  round(mod(getTimeOfLastBeatSet()/13., 13.)/13.*7.);
    }
}
