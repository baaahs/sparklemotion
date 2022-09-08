// Complex Trippy Shit #1
// Ben Bartlett

#define PI 3.1415926535897932384626433832795
#define ii vec2(0.0, 1.0)

uniform float time; // @@Time
uniform vec2 resolution; // @@Resolution

// Complex multiplication
vec2 cx_mul(vec2 a, vec2 b) {return vec2(a.x*b.x-a.y*b.y, a.x*b.y+a.y*b.x); }
// Complex division
vec2 cx_div(vec2 a, vec2 b) { return vec2(((a.x*b.x+a.y*b.y)/(b.x*b.x+b.y*b.y)),((a.y*b.x-a.x*b.y)/(b.x*b.x+b.y*b.y))); }
// Modulus
float cx_abs(vec2 z) { return length(z); }
// Complex conjugate
vec2 cx_conj(vec2 z) { return vec2(z.x, -z.y); }
// Complex argument
float cx_arg(vec2 z) { return atan(z.y, z.x); }
// Sin cos and exponential for complex numbers
vec2 cx_sin(vec2 z) { return vec2(sin(z.x) * cosh(z.y), cos(z.x) * sinh(z.y)); }
vec2 cx_cos(vec2 z) { return vec2(cos(z.x) * cosh(z.y), -sin(z.x) * sinh(z.y)); }
vec2 cx_exp(vec2 z) { return vec2(exp(z.x) * cos(z.y), exp(z.x) * sin(z.y)); }

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

// Complex math library ==========================
vec2 cx_pow(vec2 z, float n) {
    float angle = cx_arg(z);
    float r = length(z);
    float re = pow(r, n) * cos(n*angle);
    float im = pow(r, n) * sin(n*angle);
    return vec2(re, im);
}
vec2 cx_log(vec2 z) {
    float rpart = sqrt((z.x*z.x)+(z.y*z.y));
    float ipart = atan(z.y,z.x);
    if (ipart > PI) ipart=ipart-(2.0*PI);
    return vec2(log(rpart),ipart);
}
vec4 domainColoring (vec2 z, vec2 gridSpacing, float saturation, float gridStrength, float magStrength, float linePower) {
    // Adapted from https://github.com/rreusser/glsl-domain-coloring
    float carg = cx_arg(z);
    float cmod = cx_abs(z);

    float rebrt = pow((fract(z.x / gridSpacing.x) - 0.5) * 2.0, 2.);
    float imbrt = pow((fract(z.y / gridSpacing.y) - 0.5) * 2.0, 2.);

    float grid = 1.0 - (1.0 - rebrt) * (1.0 - imbrt);
    grid = pow(abs(grid), linePower);

    float circ = (fract(log2(cmod)) - 0.5) * 2.0;
    circ = pow(abs(circ), linePower) * magStrength;

    vec3 rgb = hsv2rgb(vec3(carg * 0.5 / PI, saturation, 0.5 + 0.5 * saturation - gridStrength * grid));
    rgb *= (1.0 - circ);
    rgb += circ * vec3(1.0);
    return vec4(rgb, 1.0);
}
// =============================================

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = 1.8 * (2. * fragCoord/resolution.xy - 1.);
    uv *= vec2(16./9., 1.);
    uv = vec2(-uv[1] - .2, uv[0]);

    // Function is log(1/4 + iz^-3) + 2 exp(it)
    vec2 fz = cx_log(vec2(.25, 0.0) + cx_pow(uv, -3.)) + 2. * cx_exp(time * ii);

    float refz = fz[0];
    float imfz = fz[1];
    float argfz = cx_arg(fz) / (2.*PI);
    float absfz = cx_abs(fz);

    //float map = sin(refz + imfz) - log(absfz);
    //float a=2.;
    //map = pow(absfz, a) / (pow(absfz, a) + 1.);

    fragColor = domainColoring(fz, vec2(1.0, 1.), 0.9, 1., 1., 1.);
}