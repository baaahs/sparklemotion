struct FixtureInfo {
    vec3 position; // z=left/right y=height x=front-back
    vec3 rotation; // y=left/right yaw, z=pitch (0 is up), x=roll,
    mat4 transformation;
};

struct MovingHeadParams {
    float pan;
    float tilt;
    float colorWheel;
    float dimmer;
};

#define PI 3.14159265358979323846
#define DISTANCE_FORWARD 500.0 // Inches
#define DISTANCE_SIDEWAYS 300.0 // Inches to each side

uniform FixtureInfo fixtureInfo;
uniform vec2 floorPosition; // @@XyPad
uniform float time; // @@Time

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

// @param params moving-head-params
void main(out MovingHeadParams params) {
    // +x is forward (from sheeps perspective)
    // +y is up (height)
    // +z is right (from sheeps perspective)


    vec3 target = vec3((floorPosition.y + 1.0) * DISTANCE_FORWARD / 2.0,
    0.0,
    floorPosition.x * DISTANCE_SIDEWAYS);

    // correct for the yaw-rotation of the fixture
    vec3 rotatedTarget = rotateAroundYAxis(target, fixtureInfo.rotation.y);

    //We then calculate the dx, dy, and dz differences using this rotated target position instead of the original target position. This should cause the moving head light to correctly point towards the original target position even if it starts with a nonzero yaw.
    float dx = rotatedTarget.x  - fixtureInfo.position.x;
    float dy = rotatedTarget.y  - fixtureInfo.position.y;
    float dz = rotatedTarget.z  - fixtureInfo.position.z;

    params.tilt = .0*PI - acos(dx / sqrt(dx*dx+dy*dy+dz*dz));
    params.pan = 1.5*PI+ acos(dz / sqrt(dz*dz+dy*dy));


    params.colorWheel = fixtureInfo.position.z / 10.;
    params.dimmer = 1.;

}
