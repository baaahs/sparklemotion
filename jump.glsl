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


/** Rotate a point around the y-axis (yaw)
 * @param point the point to rotate
 * @param angle the angle to rotate by
 * @return the rotated point
 */
vec3 rotateAroundYAxis(vec3 point, float angle) {
    return vec3(
    cos(angle) * point.x - sin(angle) * point.z,
    point.y,
    sin(angle) * point.x + cos(angle) * point.z
    );
}

float startOfBeatSet(float timeIn) {
    return timeIn - mod(beatInfo.beat, round(beatInterval)) / beatInfo.bpm * 60.0;
}

/** returns a timestamp of the last set of beats. e.g. if beatInterval is 4, at 120 rpm, it yields a new value
 * every 2 seconds */
float getTimeOfLastBeatSet() {
    return startOfBeatSet(time);
}

float getTimeOfPreviousLastBeatSet() {
    return startOfBeatSet(/*time inside previous BeatSet*/ time - beatInterval * beatInfo.bpm * 60.0);
}

float getTimeOfLastBeat() {
    return time - mod(beatInfo.beat, 1.) / beatInfo.bpm * 60.0;
}

float rand(float seed){
    vec2 co = vec2(1, seed);
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

float smoothRand(float seed) {
    return (sin(seed) + 1.)/2.;
}

float pan(float seed) {
    return PI + sin(seed) * 2. * 1.5 - 1.5;
}

float tilt(float seed) {
    return smoothRand(seed) * 2. * 1.2 - 1.2;
}


// @param params moving-head-params
void main(out MovingHeadParams params) {
    float lastPan = pan(getTimeOfPreviousLastBeatSet());

    params.pan = pan(getTimeOfLastBeatSet()*0.7);
    // params.pan = PI / 2.;

    // tilt: range [-1.2 ... +1.2]
    params.tilt = tilt(getTimeOfLastBeatSet()*0.5);

    params.colorWheel = 0.5;
    params.dimmer = 1.;
}
