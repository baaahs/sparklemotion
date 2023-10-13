// Beat Waves
// Adapted from http://glslsandbox.com/e#61168.1

//#extension GL_OES_standard_derivatives : enable

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;
uniform float frequency = 6.28; // @@Slider min=3.14 max=25 default=15

struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo

void main( void ) {

    vec2 position = ( gl_FragCoord.xy / resolution.xy );
    position.x -= .5;
    float num = 2.0;

    vec3 color = vec3(
    sin(frequency * position.x + 0.0 + time) * 0.5 + 0.5 - sin(beatInfo.beat * 3.14) * .5,
    sin(frequency * position.x * (sin(beatInfo.intensity) * .2 + 1.) + 2.0 + time) * 0.5 + 0.5,
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