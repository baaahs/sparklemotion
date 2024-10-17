// Huenami
// From: http://glslsandbox.com/e#44092.1

// amiga internal vendetta or coppermaster style plasma;
// gigatron base source from here;
#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;
#define t time
void main( void ) {
    vec2 p=gl_FragCoord.xy/resolution;


    p= floor(p*32.)/32.;
    vec3 a=vec3(0.5, 0.5, 0.5);
    vec3 b=vec3(0.5, 0.5, 0.5);
    vec3 c=vec3(t/4., t*0.4, t/2.);
    vec3 d=vec3(0.0, 0.33, 0.67);
    vec3 col = b+a*sin(8.0*(c+p.y+sin(p.x+p.x+t) ));
    //col*= b+a*sin(10.0*(c+p.y+cos(p.y+p.y+t) ));

    gl_FragColor=vec4(col, 1.0);
}
