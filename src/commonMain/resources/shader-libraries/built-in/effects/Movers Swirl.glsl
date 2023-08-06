struct MovingHeadParams {
    float pan;
    float tilt;
    float colorWheel;
    float dimmer;
};

uniform float time;

// @param upstream moving-head-params
// @param result moving-head-params
void main(in MovingHeadParams upstream, out MovingHeadParams result) {
    result = upstream;
    result.pan += sin(time) * .1;
    result.tilt += sin(time) * .1;
}