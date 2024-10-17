uniform float time;
uniform float rippleAmount; // @type float

// @return uv-coordinate
// @param uvIn uv-coordinate
vec2 main(vec2 uvIn) {
    vec2 p = -1.0 + 2.0 * uvIn;
    float len = length(p);
    return uvIn + (p/len)*sin(len*12.0-time*4.0)*0.1 * rippleAmount;
}