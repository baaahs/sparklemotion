// Complex math library
// Ben Bartlett
// Some stuff copied from stack overflow too :)

#define PI 3.1415926535897932384626433832795
#define ii vec2(0.0, 1.0)

// Complex multiplication
#define cx_mul(a, b) vec2(a.x*b.x-a.y*b.y, a.x*b.y+a.y*b.x)

// Complex division
#define cx_div(a, b) vec2(((a.x*b.x+a.y*b.y)/(b.x*b.x+b.y*b.y)),((a.y*b.x-a.x*b.y)/(b.x*b.x+b.y*b.y)))

// Modulus
#define cx_abs(z) length(z)

// Complex conjugate
#define cx_conj(z) vec2(z.x, -z.y)

// Complex argument
#define cx_arg(z) atan(z.y, z.x)

// Sin cos and exponential for complex numbers
#define cx_sin(z) vec2(sin(z.x) * cosh(z.y), cos(z.x) * sinh(z.y))
#define cx_cos(z) vec2(cos(z.x) * cosh(z.y), -sin(z.x) * sinh(z.y))
#define cx_exp(z) vec2(exp(z.x) * cos(z.y), exp(z.x) * sin(z.y))

// Complex power
vec2 cx_pow(vec2 z, float n) {
    float angle = cx_arg(z);
    float r = length(z);
    float re = pow(r, n) * cos(n*angle);
    float im = pow(r, n) * sin(n*angle);
    return vec2(re, im);
}

// Principal branch cut of a complex logarithm
vec2 cx_log(vec2 z) {
    float rpart = sqrt((z.x*z.x)+(z.y*z.y));
    float ipart = atan(z.y,z.x);
    if (ipart > PI) ipart=ipart-(2.0*PI);
    return vec2(log(rpart),ipart);
}

// Domain coloring for a complex-valued plot
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