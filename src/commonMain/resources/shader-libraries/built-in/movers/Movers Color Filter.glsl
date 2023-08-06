// Override colors of Moving Head fixtures. We should add a color selector here.
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


// @param inHead moving-head-params
// @param outHead moving-head-params
void main(in MovingHeadParams inHead, out MovingHeadParams outHead) {
    outHead = inHead;

    if(rotateColor){
        outHead.colorWheel = sin(time/5.);
    }
}
