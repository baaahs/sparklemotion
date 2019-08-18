// Blue Steel
// From http://glslsandbox.com/e#56594

/*
 * Original shader from: https://www.shadertoy.com/view/wt2GWK
 */

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// shadertoy emulation
#define iTime time
#define iResolution resolution

// --------[ Original ShaderToy begins here ]---------- //
#define t iTime

vec2 hash22(vec2 p) 
{
    return fract(sin(vec2(dot(p,vec2(263.135,198.742)),dot(p,vec2(624.235,321.217))))*13.635); 
}

float vorohearts( vec2 x)
{
    vec2 n = floor(-x);
    vec2 f = fract(-x);
    
	float md = 1.0;
    
    for( int j=-1; j<=3; j++ )
	{
    	for( int i=-1; i<=3; i++ )
    	{
        	vec2  g = vec2(float(i), float(j));
        	vec2  o = hash22( n + g );
	    	vec2  r = g - f + (.5+.5*sin(t+o*6.28));
    		r.x *= .8*(1.+sin(.5+sin(6.*t))*.03);
    		r.y *= .95+sin(1.5+sin(3.*t))*.07;
    		r.y += 1.08;
			r.y -= sqrt(abs(r.x))*.52;
    		float d = length(r);
        	if( d < md ) {md = d;}
    	}
    }
    return md;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = (fragCoord - 10.5*iResolution.xy)/iResolution.y;
    
    //Heart uv
    uv = uv*3.;
	float d = vorohearts(uv);
    d = smoothstep(.8, 0.15, d);
    
    //Heart color
    vec3 p = vec3(1.6*d*(.6+sin(.5+sin(6.*t))*.1), d*(1.8+sin(.5+sin(6.*t))*.3), 2.1*(1.+sin(.5+sin(6.*t))*.3));
    vec3 col = vec3(d) * p;
    
    //Background
    col += vorohearts(uv) * .35 * p;
    
    fragColor = vec4(col,1.);
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}
