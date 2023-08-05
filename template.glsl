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


struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo
uniform float beatInterval; // @@Slider min=1 max=4 step=1 default=4

uniform float time; // @@Time
uniform bool floorOnly; // @@Switch


#define PI 3.14159265358979323846
#define DISTANCE_FORWARD 500.0 // Inches
#define DISTANCE_SIDEWAYS 300.0 // Inches to each side



float startOfBeatSet(float timeIn) {
    // SIM beat:
    return time - mod(time, 0.5 * round(beatInterval));

    //return timeIn - mod(beatInfo.beat, round(beatInterval)) / beatInfo.bpm * 60.0;
}

/** returns a timestamp of the last set of beats. e.g. if beatInterval is 4, at 120 rpm, it yields a new value
 * every 2 seconds */
float getTimeOfLastBeatSet() {
    return startOfBeatSet(time);
}


float getTimeOfLastBeat() {
    return time - mod(beatInfo.beat, 1.) / beatInfo.bpm * 60.0;
}

float rand(float seed){
    vec2 co = vec2(1, seed);
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}


float pan(float value /* [-1...1]*/ ) {
    return 1.5 * PI + value * 1.5;
}

float tilt(float value /* [-1...1]*/) {
    return value * 1.2;
}

float panValue(){
    return sin(time) ;
}

float tiltValue(){
    return cos(time);
}


// @param params moving-head-params
void main(out MovingHeadParams params) {
    params.pan = pan(panValue());
    params.tilt = tilt(tiltValue());

    params.colorWheel = 0.5;
    params.dimmer = 1.;
}
