uniform float time;

// @param fragCoord uv-coordinate
// @return color
vec4 upstreamColor(vec2 fragCoord);

// @return color
// @param uvIn uv-coordinate
// @param inColor color
vec4 main(vec2 uvIn) {
    vec4 c = upstreamColor(uvIn);
    float width = .025;

    float xScan = cos(time) / 2. + .5;
    if (abs(uvIn.x - xScan) < width) {
        c.rgb *= .5;
        c.r = 0.; // 1. - c.r;
    }

    float yScan = sin(time) / 2. + .5;
    if (abs(uvIn.y - yScan) < width) {
        c.rgb *= .5;
        c.g = 0.; //  = 1. - c.g;
    }

    return vec4(c.r, c.g, c.b, 1.);
}