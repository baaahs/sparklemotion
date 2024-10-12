// Waves
// Adapted from http://glslsandbox.com/e#61168.1

//#extension GL_OES_standard_derivatives : enable

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

void main( void ) {

    vec2 position = ( gl_FragCoord.xy / resolution.xy );
    float num = 2.0;
    float frequency = 6.28;

    vec3 color = vec3(
    sin(frequency * position.x + 0.0 + time) * 0.5 + 0.5,
    sin(frequency * position.x + 2.0 + time) * 0.5 + 0.5,
    sin(frequency * position.x + 4.0 + time) * 0.5 + 0.5 + sin(time * 4.)
    );
    // color = vec3(1.0);

    color += vec3(position.y * 2.0 - 1.0);

    color = vec3(
    floor(color.r * num - 0.5) / num + 0.5,
    floor(color.g * num - 0.5) / num + 0.5,
    floor(color.b * num - 0.5) / num + 0.5
    );

    gl_FragColor = vec4( color, 1.0 );

}