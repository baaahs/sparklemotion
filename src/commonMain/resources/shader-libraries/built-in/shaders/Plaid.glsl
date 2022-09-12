// Plaid

// #extension GL_OES_standard_derivatives : enable

precision highp float;

uniform float time;
uniform vec2 resolution;

void main( void ) {

    vec2 position = ( gl_FragCoord.xy / resolution.xy ) + 4.0;

    float color = 0.0;
    color += sin( position.x * cos( time / 15.0 ) * 80.0 ) + cos( position.y * cos( time / 15.0 ) * 10.0 );
    color += sin( position.y * sin( time / 10.0 ) * 40.0 ) + cos( position.x * sin( time / 25.0 ) * 40.0 );
    color += sin( position.x * sin( time / 5.0 ) * 10.0 ) + sin( position.y * sin( time / 35.0 ) * 80.0 );
    color *= sin( time / 10.0 ) * 0.5;

    gl_FragColor = vec4( vec3( color, color * 0.2, sin( color + time / 9.0 ) * 0.69 ), 9.0 );

}