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

/** returns a timestamp of the last set of beats. e.g. if beatInterval is 4, at 120 rpm, it yields a new value
 * every 2 seconds */
float getTimeOfLastBeatSet() {
    return time - mod(beatInfo.beat, round(beatInterval)) / beatInfo.bpm * 60.0;
}

float getTimeOfLastBeat() {
    return time - mod(beatInfo.beat, 1.) / beatInfo.bpm * 60.0;
}

/* pseudo random number [0...1] */
float rand(vec2 co){
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}



// @param params moving-head-params
void main(out MovingHeadParams params) {
    if(floorOnly) {
        float randomForward = rand(vec2(1, float(getTimeOfLastBeatSet())));
        float randomSideways = rand(vec2(2, float(getTimeOfLastBeatSet())));

        vec3 target = vec3(randomForward  * DISTANCE_FORWARD,
        0.0,
        mix(-DISTANCE_SIDEWAYS, DISTANCE_SIDEWAYS, randomSideways)
        );

        // correct for the yaw-rotation of the fixture
        vec3 rotatedTarget = rotateAroundYAxis(target, fixtureInfo.rotation.y);

        //We then calculate the dx, dy, and dz differences using this rotated target position instead of the original target position. This should cause the moving head light to correctly point towards the original target position even if it starts with a nonzero yaw.
        float dx = rotatedTarget.x  - fixtureInfo.position.x;
        float dy = rotatedTarget.y  - fixtureInfo.position.y;
        float dz = rotatedTarget.z  - fixtureInfo.position.z;

        params.tilt = .0*PI - acos(dx / sqrt(dx*dx+dy*dy+dz*dz));
        params.pan = 1.5*PI+ acos(dz / sqrt(dz*dz+dy*dy));

    } else {
        params.pan = 3.14 + rand(vec2(2, float(getTimeOfLastBeatSet()))) * 2. * 1.5 - 1.5;

        // tilt: range [-1.2 ... +1.2]
        params.tilt = rand(vec2(1, getTimeOfLastBeatSet())) * 2. * 1.2 - 1.2;
    }

    params.colorWheel = rand(vec2(3, int(getTimeOfLastBeat())));
    params.dimmer = 1.;
}
