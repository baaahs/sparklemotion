// Geometric Biology
// From http://glslsandbox.com/e#45693

//Modified from: https://www.shadertoy.com/view/MslGD8

// Created by inigo quilez - iq/2013
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;
// SPARKLEMOTION GADGET: Beat { "name": "beat" }
uniform float sm_beat;

vec2 hash( vec2 p ) { p=vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))); return fract(sin(p)*18.5453); }

// return distance, and cell id
vec2 voronoi( in vec2 x )
{
    vec2 n = floor( x );
    vec2 f = fract( x );

	vec3 m = vec3( 8.0 );
    for( int j=-1; j<=1; j++ )
    for( int i=-1; i<=1; i++ )
    {
        vec2  g = vec2( float(i), float(j) );
        vec2  o = hash( n + g );
        vec2  r = g - f + (0.5+0.5*sin(time+6.2831*o));
	float d = dot( r, r );
        if( d<m.x )
            m = vec3( d, o );
    }

    return vec2( sqrt(m.x), m.y+m.z );
}

void main()
{
    vec2 p = gl_FragCoord.xy/max(resolution.x,resolution.y);
    
    // computer voronoi patterm
    vec2 c = voronoi( (14.0+6.0*sin(0.2*time))*p );

    // colorize
    vec3 col = 0.5 + 0.5*cos( c.y*6.2831 + vec3(1.0,29.0,13.0)+sin(sm_beat*2.*3.141)/2.+0.5 );
	
    gl_FragColor = vec4( col, 1.0 );
}
