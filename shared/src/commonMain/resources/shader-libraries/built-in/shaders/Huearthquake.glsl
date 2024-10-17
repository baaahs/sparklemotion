// Huearthquake
// From http://glslsandbox.com/e#46400

#ifdef GL_ES
precision mediump float;
#endif

#define PI 3.14159265358
#define TWO_PI 6.28318530718

uniform vec2 resolution;
uniform float time;

vec3 colorA = vec3(0.149, 0.141, 0.912);
vec3 colorB = vec3(1.000, 0.833, 0.224);

vec3 rgb2hsb( in vec3 c ){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz),
                 vec4(c.gb, K.xy),
                 step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r),
                 vec4(c.r, p.yzx),
                 step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)),
                d / (q.x + e),
                q.x);
}

//  Function from Iñigo Quiles
//  https://www.shadertoy.com/view/MsS3Wc
vec3 hsb2rgb( in vec3 c ){
    vec3 rgb = clamp(abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),
                             6.0)-3.0)-1.0,
                     0.0,
                     1.0 );
    rgb = rgb*rgb*(3.0-2.0*rgb);
    return c.z * mix(vec3(1.0), rgb, c.y);
}

/* Coordinate and unit utils */
vec2 coord(in vec2 p) {
    p = p / resolution.xy;
    // correct aspect ratio
    if (resolution.x > resolution.y) {
        p.x *= resolution.x / resolution.y;
        p.x += (resolution.y - resolution.x) / resolution.y / 2.0;
    } else {
        p.y *= resolution.y / resolution.x;
        p.y += (resolution.x - resolution.y) / resolution.x / 2.0;
    }
    // centering
    p -= 0.5;
    p *= vec2(-1.0, 1.0);
    return p;
}
#define rx 1.0 / min(resolution.x, resolution.y)
#define uv gl_FragCoord.xy / resolution.xy
#define st coord(gl_FragCoord.xy)
#define mx coord(u_mouse)

mat2 rot(in float angle) {
    return mat2(cos(angle), -sin(angle),
                sin(angle),  cos(angle));
}

float wave(in vec2 pt, in float f, in float v) {
    float r = length(pt);
    float a = atan(pt.y, pt.x);
    return sin(r * TWO_PI * f - v * time);
}

void main() {
    vec2 pt = st;

    float f = 10.0 * abs(sin(0.1 * time));
    float v = 10.0;
    float g = wave(pt, f, v);

    for (float k = 0.0; k < 8.0; k++) {
        float t = PI / 4.0 * k;
        vec2 offset = 0.5 * vec2(cos(t), sin(t));
        g += wave(pt + offset, f, v);
    }

    g *= 0.5;
    g = step(g, 0.0);

    vec3 rgb = mix(colorA, colorB, g);

    gl_FragColor = vec4(rgb, 1.0);
}
