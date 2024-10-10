// Red Sine
// From http://glslsandbox.com/e#60889.1

// more fanny batter (deep computed) - disco
#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;
#define PI 3.141592

void main( void ) {

    float t = time*0.9;


    vec2 position = ( gl_FragCoord.xy / resolution.xy ) - 0.5;
    position *= 2.25;
    position.y *= dot(position,position);

    position.y *= 1.0+sin(position.x*3.0)*0.2;

    float den = 0.05;
    float amp = 1.;
    float freq = 5.0+10.0;
    float offset = 0.1-tan(position.x*0.5)*5.05;

    float modifer = 6.0/abs((position.y + (amp*sin(((position.x*4.0 + t) + offset) *freq))))*den;

    vec3 colour = vec3(modifer);

    gl_FragColor = vec4( colour.r, 0., 0., 2.0 );


}