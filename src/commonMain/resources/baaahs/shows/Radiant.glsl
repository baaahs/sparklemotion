// Radiant
// From http://glslsandbox.com/e#56751.0

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.rrr + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.rrr, clamp(p - K.rrr, 0.0, 1.0), c.y);
}

vec2 rand(vec2 _n) {
    return fract(sin(vec2(_n.x*3.2,_n.y*21.5+_n.x/3.0))*120919.4+0.6);
}

float triangle(vec2 _pos) {
    vec2 pat = _pos;
    float b = 1.0;
    pat.y *= b/2.0;
    pat.x = fract(_pos.x+floor(_pos.y*b)*0.5) < 0.5 ? -pat.x : pat.x;
    if ( fract(pat.x+pat.y) > 0.5 ) {
        vec2 off = rand(floor( vec2(_pos.x+fract(floor(_pos.y*b)/2.0),_pos.y*b))*vec2(1.12,2.02))*2.0-1.0;
        return length(floor( vec2(_pos.x+fract(floor(_pos.y*b)/2.0),_pos.y*b) )-vec2(fract(floor(_pos.y*b)/2.0),0.0)-off);
    } else {
        vec2 off = rand(floor( vec2(_pos.x+fract(floor(_pos.y*b+1.0)/2.0),_pos.y*b))*vec2(4.011,15.32))*2.0-1.0;
        return length(floor( vec2(_pos.x+fract(floor(_pos.y*b+1.0)/2.0),_pos.y*b) )-vec2(fract(floor(_pos.y*b+1.0)/2.0),-b/2.0)-off);
    }
}

void main(void) {
    vec2 pos = (gl_FragCoord.xy/resolution.x);
    pos -= vec2(1.0,resolution.y/resolution.x)/2.0;
    pos *= 90.0;
    float col = max(sin(triangle(pos)/3.0-time*3.0),0.0);

    col = 0.45+col*col;
    gl_FragColor = vec4(hsv2rgb(vec3(time/8.0,1.0-col/3.0,col)),1.0);
}