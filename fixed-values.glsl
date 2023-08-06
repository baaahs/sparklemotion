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
    // float bpm =  125.;
    // return time - mod(time, 60. / bpm  * round(beatInterval));

    return timeIn - mod(beatInfo.beat, round(beatInterval)) / beatInfo.bpm * 60.0;
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

float getSeed(){
    float counter = getTimeOfLastBeatSet() * beatInfo.bpm / 60.;
    return counter / round(beatInterval);
}


float pan(float value /* [-1...1]*/ ) {
    return 3. * PI /2. + value * 1.5*  PI;
}

float tilt(float value /* [-1...1]*/) {
    return value * 1.2;
}

float panValue(){

    int t = int(mod(getSeed(), 8.));
    if(t == 0) return -1.;
    else if(t == 1) return -0.4;
    else if(t == 2) return 0.3;
    else if(t == 3) return 0.8;
    else if(t == 4) return 1.;
    else if(t == 5) return 0.4;
    else if(t == 6) return 0.1;
    else if(t == 7) return -0.5;
    else if(t == 7) return -0.8;
    return 0. ;
}

float tiltValue(){
    int t = int(mod(getSeed(), 5.));
    if(t == 0) return -1.;
    else if(t == 1) return -0.4;
    else if(t == 2) return -0.6;
    else if(t == 3) return -0.3;
    else if(t == 4) return -0.7;

    return 0. ;
}


// @param params moving-head-params
void main(out MovingHeadParams params) {
    params.pan = pan(panValue());
    params.tilt = tilt(tiltValue());

    params.colorWheel =  round(mod(getTimeOfLastBeatSet()/13., 13.)/13.*7.);
    params.colorWheel =  panValue();
    params.dimmer = 1.;
}
