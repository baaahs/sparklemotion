struct MovingHeadParams {
    float pan;
    float tilt;
    float colorWheel;
    float dimmer;
};

// @return moving-head-params
MovingHeadParams main() {
    MovingHeadParams params;
    params.pan = .5;
    params.tilt = .5;
    params.colorWheel = 0.;
    params.dimmer = 1.;
    return params;
}