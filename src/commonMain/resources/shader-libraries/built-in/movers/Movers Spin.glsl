// Spin the Moving Heads together, crossed or indepedant.
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
uniform float time;// @@Time
uniform float speed;// @@Slider min=.5 max=5 default=2.
uniform float offset;// @@Slider min=0 max=3.14 default=0
uniform bool mirrored;// @@Switch

/* pseudo random number [0...1] */
float rand(float seed){
    vec2 co = vec2(1, seed);
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

float getTimeOfLastBeat() {
    return time - mod(beatInfo.beat, 1.) / beatInfo.bpm * 60.0;
}

#define PI 3.14159265358979323846

// @param params moving-head-params
void main(out MovingHeadParams params) {
    bool isLeft = fixtureInfo.position.z < 0.;
    float rotation = 0.5 + (cos(speed*time/3.)+sin(speed*time/2.)) / 2.0 / 2.0;// [0...1]
    params.pan = mix(0.0, (2.) * PI, mirrored && isLeft ? rotation : 1. - rotation) + (isLeft ? offset : 0.);

    params.tilt = .3+(1. + cos(1. + time))/2.0;
    params.colorWheel = rand(getTimeOfLastBeat());
}
