/*{
  "CREDIT": "by mojovideotech",
  "DESCRIPTION": "from http://glslsandbox.com/e#35553.0",
  "CATEGORIES": [
    "fluid",
    "liquid"
  ],
  "INPUTS": [
  	{
			"NAME" :	"rate1",
			"TYPE" :	"float",
			"DEFAULT" :	1.9,
			"MIN" :	-3.0,
			"MAX" :	3.0
	},
	 {
			"NAME" :	"rate2",
			"TYPE" :	"float",
			"DEFAULT" :	0.6,
			"MIN" :	-3.0,
			"MAX" :	3.0
	},
	{
			"NAME" :	"loopcycle",
			"TYPE" :	"float",
			"DEFAULT" :	85.0,
			"MIN" :	20.0,
			"MAX" :	100.0
	},
	{
			"NAME" :	"color1",
			"TYPE" :	"float",
			"DEFAULT" :	0.45,
			"MIN" :	-2.5,
			"MAX" :	2.5
	},
	{
			"NAME" :	"color2",
			"TYPE" :	"float",
			"DEFAULT" :	1.0,
			"MIN" :	-1.25,
			"MAX" :	1.125
	},
	{
			"NAME" :	"cycle1",
			"TYPE" :	"float",
			"DEFAULT" :	1.33,
			"MIN" :	0.01,
			"MAX" :	3.1459
	},
	{
			"NAME" :	"cycle2",
			"TYPE" :	"float",
			"DEFAULT" :	0.22,
			"MIN" :	-0.497,
			"MAX" :	0.497
	},
	{
			"NAME" :	"nudge",
			"TYPE" :	"float",
			"DEFAULT" :	0.095,
			"MIN" :	0.001,
			"MAX" :	0.01
	},
	{
      			"NAME" :	"depthX",
      			"TYPE" : 	"float",
      			"DEFAULT" :	0.85,
      			"MIN" : 	0.001,
      			"MAX" :		0.9
    	},
    	{
      			"NAME" :	"depthY",
      			"TYPE" :	"float",
      			"DEFAULT" :	0.25,
      			"MIN" : 	0.001,
      			"MAX" :		0.9
    	}
  ]
}*/

///////////////////////////////////////////
// ColorDiffusionFlow  by mojovideotech
//
// based on :
// glslsandbox.com/\e#35553.0
//
// Creative Commons Attribution-NonCommercial-ShareAlike 3.0
///////////////////////////////////////////


#ifdef GL_ES
precision mediump float;
#endif

#define 	pi   	3.141592653589793

void main() {
    float T = TIME * rate1;
    float TT = TIME * rate2;
    vec2 p=(2.*isf_FragNormCoord);
    for(int i=1;i<11;i++) {
        vec2 newp = p;
        float ii = float(i);
        newp.x+=depthX/ii*sin(ii*pi*p.y+T*nudge+cos((TT/(5.0*ii))*ii));
        newp.y+=depthY/ii*cos(ii*pi*p.x+TT+nudge+sin((T/(5.0*ii))*ii));
        p = newp +log(DATE.w)/loopcycle;
    }
    vec3 col=vec3(cos(p.x+p.y+3.0*color1)*0.5+0.5,sin(p.x+p.y+6.0*cycle1)*0.5+0.5,(sin(p.x+p.y+9.0*color2)+cos(p.x+p.y+12.0*cycle2))*0.25+.5);
    gl_FragColor=vec4(col*col, 1.0);
}