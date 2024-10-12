// Yas Queen
// From http://glslsandbox.com/e#46613

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;
// EXAMPLE FROM https://www.shadertoy.com/view/lstfRH
#define PI 3.141592653589793

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}
void main( void ) {

    vec3 col;
    float x = gl_FragCoord.x + sin(time/PI) * resolution.x / 4.0;
    float y = gl_FragCoord.y + cos(time/PI) * resolution.y / 2.0;
    float hue = sin(x / resolution.x) + cos(y / resolution.y) + sin((x + y) / 500.0) + cos(sqrt(x * x + y * y) / 100.0);

    col = hsv2rgb(vec3(hue + time/8.0, 1.0, mod(time, 1000.0)));

    gl_FragColor = vec4(col, 1);
}
