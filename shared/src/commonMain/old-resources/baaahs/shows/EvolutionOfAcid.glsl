// Evolution of Acid
// From http://glslsandbox.com/e#45963

#ifdef GL_ES
precision highp float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

#define PI 3.14159265358979323846

float box(vec2 _st, vec2 _size, float _smoothEdges){
	_size = vec2(1.75)-_size*0.75;
	vec2 aa = vec2(_smoothEdges*0.5);
	vec2 uv = smoothstep(_size,_size+aa,_st);
	uv *= smoothstep(_size,_size+aa,vec2(1.0)-_st);
	return uv.x*uv.y;
}

vec2 tile(vec2 _st, float _zoom){
	_st *= _zoom;
	return fract(_st);
}

vec2 rotate2D(vec2 _st, float _angle, vec2 shift){
	_st -= 0.5 + shift.x;
	_st =  mat2(cos(_angle),-sin(_angle),
	sin(_angle),cos(_angle)) * _st;
	_st += 0.5 + shift.y;
	return _st;
}

void main(void){
	vec2 v = (gl_FragCoord.xy - resolution/2.0) / min(resolution.y,resolution.x) * 5.0;
	vec2 vv = v; vec2 vvv = v;
	float tm = (time + 100.)*0.02;
	vec2 mspt = (vec2(
	sin(tm)+cos(tm*0.2)+sin(tm*0.5)+cos(tm*-0.4)+sin(tm*1.3),
	cos(tm)+sin(tm*0.1)+cos(tm*0.8)+sin(tm*-1.1)+cos(tm*1.5)
	)+15.0)*0.03;


	vec2 simple = (vec2(sin(tm), cos(tm)) + 1.5) * 0.15;
	float R = 0.0;
	float RR = 0.0;
	float RRR = 0.0;
	float a = (.6-simple.x)*6.2;
	float C = cos(a);
	float S = sin(a);
	vec2 xa=vec2(C, -S);
	vec2 ya=vec2(S, C);
	vec2 shift = vec2( 1.2, 1.62);
	float Z = 1.0 + simple.y*6.0;
	float ZZ = 1.0 + (simple.y)*6.2;
	float ZZZ = 1.0 + (simple.y)*6.9;

	vec2 b = gl_FragCoord.xy/(resolution);
	b = rotate2D(b, PI*Z, 0.05*xa);
	//b = vec2(box(b,vec2(1.1),0.95));

	for ( int i = 0; i < 25; i++ ){
		float br = dot(b,b);
		float r = dot(v,v);
		if ( r > sin(tm) + 3.0 )
		{
			r = (sin(tm) + 3.0)/r ;
			v.x = v.x * r + 0.;
			v.y = v.y * r + 0.;
		}
		if ( br > 0.75 )
		{
			br = (0.56)/br ;
			//v.x = v.x * r + 0.;
			//v.y = v.y * r + 0.;
		}

		R *= 1.05;
		R += br;//b.x;
		if(i < 24){
			RR *= 1.05;
			RR += br;//b.x;
			if(i <23){
				RRR *= 1.05;
				RRR += br;//b.x;
			}
		}

		v = vec2( dot(v, xa), dot(v, ya)) * Z + shift;
		//b = vec2( dot(b.xy, xa), dot(b.xy, ya)) * Z + shift;
		//b = rotate2D(vec2( dot(v, xa), dot(v, ya)), PI*Z, ya);
		//b = vec2( dot(b, xa), dot(b, ya));
		b = vec2(box(v,vec2(5.),0.9)) + shift * 0.42;
	}
	float c = ((mod(R,2.0)>1.0)?1.0-fract(R):fract(R));
	float cc = ((mod(RR,2.0)>1.0)?1.0-fract(RR):fract(RR));
	float ccc = ((mod(RRR,2.0)>1.0)?1.0-fract(RRR):fract(RRR));
	gl_FragColor = vec4(ccc,cc,c, 1.0);
}
