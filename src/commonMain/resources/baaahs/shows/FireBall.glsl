// Fire Ball
// From http://glslsandbox.com/e#61108.0

// Fire ball by David Robles

#ifdef GL_ES
precision mediump float;
#endif

//#extension GL_OES_standard_derivatives : enable
#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;

const float PI = 3.141592;

vec2 hash( vec2 p ) // replace this by something better
{
    p = vec2( dot(p,vec2(127.1,311.7)), dot(p,vec2(269.5,183.3)) );
    return -1.0 + 2.0*fract(sin(p)*43758.5453123);
}

float noise( in vec2 p )
{
    const float K1 = 0.366025404; // (sqrt(3)-1)/2;
    const float K2 = 0.211324865; // (3-sqrt(3))/6;

    vec2  i = floor( p + (p.x+p.y)*K1 );
    vec2  a = p - i + (i.x+i.y)*K2;
    float m = step(a.y,a.x);
    vec2  o = vec2(m,1.0-m);
    vec2  b = a - o + K2;
    vec2  c = a - 1.0 + 2.0*K2;
    vec3  h = max( 0.5-vec3(dot(a,a), dot(b,b), dot(c,c) ), 0.0 );
    vec3  n = h*h*h*h*vec3( dot(a,hash(i+0.0)), dot(b,hash(i+o)), dot(c,hash(i+1.0)));
    return dot( n, vec3(70.0) );
}

float circleShape(vec2 coord, vec2 pos){
    float dist = distance(coord, pos);
    return clamp(   log(dist*(15.+4.*noise(vec2(time*.8))))  ,0.,1.);
}

vec2 sineWave(vec2 p){
    float Ax = .05;
    float wx = 1.50 * PI;
    float x = sin(wx * p.x) * Ax * sin(noise(vec2(time)));

    float Ay = .05;
    float wy = PI * 10.;
    float y = sin(wy*p.y) * Ay * noise(vec2(time));

    return vec2(p.x + x, p.y + y);
}

void main( void ) {

    vec2 pos = gl_FragCoord.xy / resolution.xy;
    vec2 uv = pos;

    uv += vec2(-.5, -.5);
    uv *= vec2(2.3, 1.3);

    float luz = clamp(1.05 - (pow(uv.x, 2.) + pow(uv.y * 1.6, 6.))*2., 0., 1.);
    //vec3 color = vec3(0.3059, 0.1922, 0.0431);
    vec3 color = vec3(0.7333, 0.2902, 0.0314);
    //vec3 color = vec3(0.3882, 0.1686, 0.251);
    float grad = circleShape(sineWave(pos), vec2(.5, .32));

    float ruido = 0.;

    pos *= 5.0;
    float xoff = 1.05;
    float yoff = 2.1;
    mat2 m = mat2( 1.6,  1.2, -1.2,  1.6 );
    ruido = 0.7500*noise(vec2(abs(pos.x-time*xoff), abs(pos.y-time*yoff))); pos = m*pos;
    ruido += 0.2500*noise(vec2(abs(pos.x-time*xoff), abs(pos.y-time*yoff))); pos = m*pos;
    ruido += 0.5000*noise(vec2(abs(pos.x-time*xoff), abs(pos.y-time*yoff))); pos = m*pos;
    ruido += 0.1250*noise(vec2(abs(pos.x-time*xoff), abs(pos.y-time*yoff))); pos = m*pos;


    ruido = 0.5 + 0.5*ruido;
    ruido = clamp(ruido, 0., 1.);

    /////////////////////////////////////////////////////////////
    pos = uv;
    pos *= 3.0;
    float ruido2 = 0.;
    xoff = 1.5;
    yoff = 1.5;
    m = mat2( -2.1,  .5, -.5,  2.1 );
    ruido2 = 0.2500*noise(vec2(abs(pos.x-time*xoff), abs(pos.y-time*yoff))); pos = m*pos;
    ruido2 += 0.5000*noise(vec2(abs(pos.x-time*xoff), abs(pos.y-time*yoff))); pos = m*pos;
    ruido2 += 0.1250*noise(vec2(abs(pos.x-time*xoff), abs(pos.y-time*yoff))); pos = m*pos;
    ruido2 += 0.0625*noise(vec2(abs(pos.x-time*xoff), abs(pos.y-time*yoff))); pos = m*pos;

    ruido2 = 0.5 + 0.5*ruido2;
    ruido2 = clamp(ruido2, 0., 1.);

    float f = 0.;
    f += 1.0 - ( (1.0-luz) / (ruido2 + 0.001) );
    f /= grad;
    f /= ruido;

    gl_FragColor = vec4(f*color*luz*(.5-.5*noise(vec2(time*.8))), 1.);
    //gl_FragColor = vec4(color/grad, 1.);

}