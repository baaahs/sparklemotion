// Gadzooks
// From: http://glslsandbox.com/e#43036.0

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

void main( void ) {

    vec2 p = gl_FragCoord.xy / resolution.xy;
    p = p * 2.0 - 1.0;
    p.x *= resolution.x / resolution.y;

    float col = 0.0;

    //mandelbrote
    //vec2 z = vec2(0.0, 0.0);
    //juillaset
    vec2 z = p;
    vec2 ms = mouse.xy * 2.0 - 1.0;
    ms.x *= resolution.x / resolution.y;

    for(int i = 0; i < 5; i++)
    {
        //mandelbrote
        z = vec2(z.x * z.x - z.y * z.y + cos(time) *2.0, 2.0 * z.x * z.y) + ms;
        //julliaset
        //z = vec2(z.x * z.x - z.y * z.y, 2.0 * z.x * z.y) + P;
    }
    col = length(z);

    gl_FragColor = vec4(vec3(z.x, z.y, col), 1.0);

}
