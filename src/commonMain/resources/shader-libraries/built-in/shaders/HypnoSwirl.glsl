// Hypno-Swirl
// From http://glslsandbox.com/e#46292

#ifdef GL_ES
    precision mediump float;
#endif

// System functions to define needed vars and such stuff
#define COLORS(num) int colors = num; float segment = 1.0 / float(num); float offset = segment / 2.0;
#define COLOR_START(col) color = gradient(col, col, 0.0, offset, value); vec3 start_col = col; vec3 last_col = col;
#define COLOR_END() BLEED(last_col, start_col);
#define COLOR_ACTUAL(col, step) color = gradient(color, col, offset + bleed, offset + segment - bleed, value); offset=offset+step; last_col = col;

// User facing functions
#define COLORL(col, length) BLEED(last_col, col); COLOR_ACTUAL(col, length);
#define BLEED(col_l, col_r) color = gradient(color, col_l, offset - bleed, offset, value); color = gradient(color, col_r, offset, offset + bleed, value);
#define COLOR(col) COLORL(col, segment);
uniform float time;
uniform vec2 resolution;

vec3 hsv2rgb(vec3);
vec4 gradient(vec4 base, vec3 stop, float start, float end, float grayscale);
vec4 gradient(vec3 base, vec3 stop, float start, float end, float grayscale);

const float pi = 3.141592654;

// Spiral settings
const int segments = 4;
const float fold = 6.0;
const float fold_k = 0.1;
const float delay = 15.0; // In seconds (?)
const float bleed_base = 0.01;
const float brightness = 0.5;

// Colors are HSV
// Some tips: V (third param) controls how much the color "glows"
//   basically. This allows to, for example, mute other color's
//   value, and make a nice effect of glow for one color.
const vec3 col1 = vec3(0.65, 0.8, 1.0); // Rich blue
const vec3 col2 = vec3(0.85, 1.0, 0.9); // Pink cotton
const vec3 col3 = vec3(1.00, 1.0, 0.8); // Red
const vec3 col4 = vec3(0.15, 1.0, 0.9); // Banana yellow
const vec3 col5 = vec3(0.45, 1.0, 0.9); // Minty green

void main(void) {
    vec2 uv = (gl_FragCoord.xy - 0.5 * resolution.xy) / resolution.yy;
    float r = length(uv);           // Distance
    float theta = atan(uv.y, uv.x); // Radial coord
    float value = float(fract(
        float(segments) / 2.0 * theta / pi +
        fold * pow(r, fold_k) - float(segments) * time / delay
    ));
    vec4 color;
    // c2 - bleed - c1 - bleed - c2
    float bleed = bleed_base * (1.0 - r);
    bleed = clamp(bleed, 0.0, bleed_base);

    // COLORS(N) defines the amount of gradient segments for every segment.
    // Mismatch of N and amount of COLOR() (including start) statements is undefined.

    // To "enable" a preset, remove / after active one, and add it to one you want to use
    /* Cotton candy "preset" */
    COLORS(2);
    COLOR_START(col1);
    COLOR(col2);
    /**/

    /* Something I dunno *
    COLORS(5);
    COLOR_START(col1);
    COLOR(col2);
    COLOR(col3);
    COLOR(col4);
    COLOR(col5);
    /**/

    /* Black and yellow *
    COLORS(2);
    COLOR_START(vec3(0.0, 0.0, 0.1));
    COLOR(vec3(0.121, 1.0, 1.0));
    /**/

    /* Glowing blue *
    COLORS(4);
    COLOR_START(vec3(0.5, 1.0, 1.0));
    COLOR(vec3(0.5, 0.5, 0.2));
    COLOR(vec3(0.55, 1.0, 0.8));
    COLOR(vec3(0.5, 0.5, 0.2));
    /**/


    COLOR_END();
    //

    //MAYBE not needed idk
    //color = gradient(color, col2, 0.75 + bleed, 1.00, value);/**/
    gl_FragColor = color;
}

vec3 hsv2rgb(vec3 c) {
    c.z = c.z * brightness;
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec4 gradient(vec4 base, vec3 stop, float start, float end, float grayscale) {
    return mix(base, vec4(hsv2rgb(stop), 1.0), smoothstep(start, end, grayscale));
}

vec4 gradient(vec3 base, vec3 stop, float start, float end, float grayscale) {
    return gradient(vec4(hsv2rgb(base), 1.0), stop, start, end, grayscale);
}
