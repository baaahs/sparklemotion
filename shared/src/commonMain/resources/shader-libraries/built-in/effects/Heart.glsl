// Heart
// From http://mathworld.wolfram.com/HeartSurface.html

struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};

uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo

// @param fragCoord uv-coordinate
// @return color
vec4 upstreamColor(vec2 fragCoord);

float f(vec3 p) {
    vec3 pp = p * p;
    vec3 ppp = pp * p;
    float a = pp.x + 2.25 * pp.y + pp.z - 1.0;
    return a * a * a - (pp.x + 0.1125 * pp.y) * ppp.z;
}

// Bisection solver for y
float h(float x, float z) {
    float a = 0.0, b = 0.75, y = 0.5;
    for (int i = 0; i < 10; i++) {
        if (f(vec3(x, y, z)) <= 0.0)
        a = y;
        else
        b = y;
        y = (a + b) * 0.5;
    }
    return y;
}

// Analytical gradient
// (-2 x z^3+6 x (-1.+x^2+2.25 y^2+z^2)^2)
// (-0.225 y z^3+13.5 y (-1.+x^2+2.25 y^2+z^2)^2)
// (z (-3 x^2 z-0.3375 y^2 z+6 (-1.+x^2+2.25 y^2+z^2)^2))
vec3 normal(vec2 p) {
    vec3 v = vec3(p.x, h(p.x, p.y), p.y);
    vec3 vv = v * v;
    vec3 vvv = vv * v;
    float a = -1.0 + dot(vv, vec3(1, 2.25, 1));
    a *= a;

    return normalize(vec3(
    -2.0 * v.x * vvv.z +  6.0 * v.x * a,
    -0.225 * v.y * vvv.z + 13.5 * v.y * a,
    v.z * (-3.0 * vv.x * v.z - 0.3375 * vv.y * v.z + 6.0 * a)));
}

void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
    vec3 p = (vec3((2.0 * fragCoord.xy - iResolution.xy) / min(iResolution.y, iResolution.x), 0)) * 2.;
    float beat = beatInfo.beat;
    float s = sin(beat * 4.0);
    s *= s;
    s *= s;
    s *= s;
    s *= s;
    s *= 0.1;
    vec3 tp = p * vec3(1.0 + s, 1.0 - s, 0.0) * 2.0;

    if (f(tp.xzy) <= 0.0) {
        vec3 n = normal(tp.xy);
        float diffuse = dot(n, normalize(vec3(-1, 1, 1))) * 0.5 + 0.5;
        float specular = pow(max(dot(n, normalize(vec3(-1, 2, 1))), 0.0), 64.0);
        float rim = 0.; //1.0 - dot(n, vec3(0.0, 1.0, 0.0));
        fragColor = vec4(diffuse * vec3(1.0, 0, 0) + specular * vec3(0.8) + rim * vec3(0.5), 1.);
    }
    else
    fragColor = upstreamColor(fragCoord);
}