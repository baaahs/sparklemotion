// Spastic Rainbow
// From http://glslsandbox.com/e#56433

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;


#define PI 3.14159265

float rcos(float x,float phase){
	return (cos((x+phase)*2.*PI)+.5);	

}
highp float rand(vec2 co)
{
    highp float a = 12.9898;
    highp float b = 78.233;
    highp float c = 43758.5453;
    highp float dt= dot(co.xy ,vec2(a,b));
    highp float sn= mod(dt,3.14);
    return fract(sin(sn) * c);
}

vec3 rain(float x){
	return vec3(rcos(x,0.),rcos(x,2./3.),rcos(x,1./3.));
}

void main() {
	vec2 st = gl_FragCoord.xy/resolution;
		
	for(float i=0.;i<20.0;i++){
		vec2 pos=vec2(rand(vec2(i,i)),rand(vec2(i+1.,i+2.)));
		
		vec2 sst=st-vec2(mod(pos+mod(time,1.),1.));
	
		gl_FragColor.rgb+=(rain(rand(pos))*.4)/(length(sst)*60.0);
	}
	
	gl_FragColor.a=1.0;
}
