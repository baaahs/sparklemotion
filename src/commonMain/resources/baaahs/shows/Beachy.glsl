// Beachy

#ifdef GL_ES
precision mediump float;
#endif

//#extension GL_OES_standard_derivatives : enable

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

// The MIT License
// Copyright © 2013 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

vec2 hash( vec2 p ) { p=vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))); return fract(sin(p)*18.5453); }

float rand(float n){return fract(sin(n) * 43758.5453123);}

float noise(float p){
    float fl = floor(p);
    float fc = fract(p);
    return mix(rand(fl), rand(fl + 1.0), fc);
}

float rand(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}


float noiseg(vec2 n) {
    const vec2 d = vec2(0.0, 1.0);
    vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));
    return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
}

vec3 color(vec3 c) {

    vec2 o = hash(c.yz);

    if(c.z < 2.0) {
        return vec3(1.0, 0.7 + 0.3*noiseg(o), 0.0);
    } else if(c.z >= 2.0 && c.z <= 3.0) {
        return vec3(0.7 + 0.3*noiseg(o));
    } else {
        return vec3(0.0, 0.4 + 0.3*noiseg(o), 1.0);
    }
    return vec3(0.0);
}

// return distance, and cell id
vec3 voronoi(vec2 x )
{
    vec2 n = floor( x );
    vec2 f = fract( x );

    vec3 m = vec3( 8.0 );
    vec2 om = vec2(0.0);
    for( int j=-1; j<=1; j++ )
    for( int i=-1; i<=1; i++ )
    {
        vec2  g = vec2( float(i), float(j) );
        vec2 g2 = n + g;
        vec2  o = hash(g2);

        vec2  r = g - f + o;
        if(g2.y >= 2.0)
        r = g - f + (0.5+0.99*sin(time*0.8+4.6*o));
        float d = dot( r, r );
        if( d<m.x ) {
            m = vec3( d, o );
            om = g2;
        }
    }

    return vec3( sqrt(m.x), om.x, om.y );
}
void main(void)
{
    float maxr = max(resolution.x,resolution.y);
    float yf = gl_FragCoord.y / maxr;
    vec2 p = gl_FragCoord.xy / maxr;
    vec3 cl = voronoi(vec2(p.x * 25.0, p.y * (10.0 + 1000.0 * yf*yf*yf)));

    gl_FragColor = vec4( color(cl), 1.0 );
}