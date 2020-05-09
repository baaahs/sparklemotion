// Orange Tang
// From http://glslsandbox.com/e#56718

precision mediump float;

uniform float time;
//uniform vec2 resolution;

// SPARKLEMOTION GADGET: Slider {name: "MouseX", initialValue: 0.5, minValue: 0.0, maxValue: 1.0}
uniform float mouseX;

// SPARKLEMOTION GADGET: Slider {name: "MouseY", initialValue: 0.5, minValue: 0.0, maxValue: 1.0}
uniform float mouseY;

//uniform vec2 mouse;
//varying vec2 surfacePosition;

void main( void ) {
	vec2 p = gl_FragCoord.xy;
	float speed = 0.25;
	vec3 color = vec3(1.,0.5,.25);
	vec2 loc = vec2(
		cos(time/4.0*speed)/1.9-cos(time/2.0*speed)/3.8,
		sin(time/4.0*speed)/1.9-sin(time/2.0*speed)/3.8
	);
	float depth;
	for(int i = 0; i < 50; i+=1){
		p = vec2(p.x*p.x-p.y*p.y, 2.0*p.x*p.y)+loc;
		depth = float(i);
		if((p.x*p.x+p.y*p.y) >= mouseY*4.0) break;
	}
	gl_FragColor = vec4(clamp( (mouseX+.5)*color*depth*0.05, 0.0, 1.0), 1.0 );
}
