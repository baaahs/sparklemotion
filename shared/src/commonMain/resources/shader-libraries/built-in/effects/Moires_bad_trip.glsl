uniform float time; // @@Time
uniform vec2 resolution; // @@Resolution

uniform float depth; // @@Slider default=0.5 min=0.1 max=0.9
uniform float iterations; // @@Slider default=20. min=5. max=50.
uniform float exponent; // @@Slider default=1.5 min=-3. max=3.
uniform float beatResponsiveness; // @@Slider default=0.0 min=0.0 max=0.5
uniform float start_radius; //@@Slider default=2.75 min=1. max = 5.
uniform float squareness; //@@Slider default=30. min=1. max=100.

struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};

uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo

float tanh_approx(float x) {
    float x2 = x*x;
    return clamp(x*(27.0 + x2)/(27.0+9.0*x2), -1.0, 1.0);
}

vec3 offset(float z) { float a = z; vec2 p = -0.1*vec2(1.2, .6)*(vec2(cos(a), sin(a*sqrt(2.0))) + vec2(cos(a*sqrt(0.75)), sin(a*sqrt(0.5)))); return vec3(p, z); }
vec3 doffset(float z) { float eps = 0.05; return 0.5*(offset(z + eps) - offset(z - eps))/(2.0*eps); }
vec3 ddoffset(float z) { float eps = 0.05; return 0.5*(doffset(z + eps) - doffset(z - eps))/(2.0*eps); }

// @return uv-coordinate
// @param uvIn uv-coordinate
vec2 main(vec2 uvIn) {
    float tm  = depth*time*beatInfo.bpm/60.0;
    vec3 ro   = offset(tm);
    vec3 dro  = doffset(tm);
    vec3 ddro = ddoffset(tm);

    vec3 ww = normalize(dro);
    vec3 uu = normalize(cross(normalize(vec3(0.0, 1.0, 0.0)+ddro), ww));
    vec3 vv = cross(ww, uu);

    float lp = length(uvIn);
    float cosScale = squareness * (1. - beatResponsiveness * beatInfo.intensity);

    const float outer_rad=0.5;
    float rdd = start_radius + outer_rad*pow(lp, exponent)*tanh_approx(lp+3.*0.5*(cos(cosScale*uvIn.x)+1.0)*0.5*(cos(cosScale*uvIn.y)+1.0));

    vec3 rd = normalize(uvIn.x*uu + uvIn.y*vv + rdd*ww);

    float nz_sheep = ro.z / depth;
    
    float pz_sheep = depth*nz_sheep + depth * iterations;
    float pd_sheep = (pz_sheep - ro.z) / rd.z;
    vec3 pp_sheep = ro + rd*pd_sheep;
    vec3 off_sheep = offset(pp_sheep.z);
    vec2 p_sheep = (pp_sheep-off_sheep*vec3(1.0, 1.0, 0.0)).xy;

    float sheep_scale=0.6;
    vec2 sheep_xy = p_sheep / sheep_scale;

    return sheep_xy;
}
