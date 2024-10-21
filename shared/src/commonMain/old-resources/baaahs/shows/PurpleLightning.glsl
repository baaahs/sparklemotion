// Purple Lightning

// Lightning
// By: Brandon Fogerty
// bfogerty at gmail dot com 
// xdpixel.com


#ifdef GL_ES
precision lowp float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;


float Hash( vec2 p)
{
     vec3 p2 = vec3(p.xy,1.0);
    return fract(sin(dot(p2,vec3(37.1,61.7, 12.4)))*3758.5453123);
}

float noise(in vec2 p)
{
    vec2 i = floor(p);
     vec2 f = fract(p);
     f *= f * (3.0-2.0*f);

    return mix(mix(Hash(i + vec2(0.,0.)), Hash(i + vec2(1.,0.)),f.x),
               mix(Hash(i + vec2(0.,1.)), Hash(i + vec2(1.,1.)),f.x),
               f.y);
}

float fbm(vec2 p)
{
     float v = 0.0;
     v += noise(p*1.0)*.5;
     v += noise(p*2.)*.25;
     v += noise(p*4.)*.125;
     return v * 1.0;
}

void main( void ) 
{

	vec2 uv = ( gl_FragCoord.xy / resolution.xy ) * 2.0 - 1.0;
	float limit = resolution.x/resolution.y;
	uv.x *= limit;
	
	float t = time*0.125;
	
		
	vec3 finalColor = vec3( 0.0 );
	for( int i=1; i < 15; ++i )
	{
		float offset = 0.0;//noise(vec2(time*float(i))) / float(i);
		float hh = float(i) * 0.1;
		float t = abs(1.0 / ((-0.5 + fbm( vec2(uv.x + offset - 15.0*t/float(i), uv.y + offset + 10.0*t/float(i))))*475.));
		finalColor +=  t * vec3( hh+0.1, 0.5, 2.0 );
	}
	
	gl_FragColor = vec4( finalColor, 1.0 );

}
