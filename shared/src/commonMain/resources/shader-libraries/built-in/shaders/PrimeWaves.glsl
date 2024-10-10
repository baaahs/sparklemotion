/*{
  "CREDIT": "by mojovideotech",
  "CATEGORIES" : [
    "Generator",
    "waves"
  ],
  "DESCRIPTION" : "",
  "INPUTS" : [
    {
      "NAME" : "center",
      "TYPE" : "point2D",
       "DEFAULT": [
		-2,
		-1
	  ],
      "MAX" : [
        10,
        10
      ],
      "MIN" : [
        -10,
        -10
      ]
    },
    {
		"NAME": "rate",
		"TYPE": "float",
		"DEFAULT": -1,
		"MIN": -3,
		"MAX": 3
	},
	{
		"NAME": "zoom",
		"TYPE": "float",
		"DEFAULT": 5,
		"MIN": -10,
		"MAX": 10
	},
	{
		"NAME": "depth",
		"TYPE": "float",
		"DEFAULT": 0.6,
		"MIN": 0.0,
		"MAX": 1.0
	},
	{
		"NAME": "rxy",
		"TYPE": "float",
		"DEFAULT": 11,
		"MIN": 1,
		"MAX": 17
	},
	{
		"NAME": "rxz",
		"TYPE": "float",
		"DEFAULT": 13,
		"MIN": 1,
		"MAX": 17
	}
  ]
}
*/

// PrimeWaves by mojovideotech
// based on:
// glslsandbox.com/e#21344.0

#ifdef GL_ES
precision highp float;
#endif

uniform float time; // @@Time
uniform vec2 resolution; // @@Resolution
uniform float speed; // @@Slider default=3.0 min=1.0 max=5.0


struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo

float beatIntegral() {
	float t = mod(beatInfo.beat, 1.);
	float POWER = 4.; // Adjusts sharpnett of the curve
	float OFFSET = 0.0; // Adjusts future-offset of curve. OFFSET=0.5 means the steepest part happens between beats.
	return 1. - pow(1. - mod(t + OFFSET, 1.0), POWER);
}

float pulsedTime() {
    float timeAdjustment = beatIntegral() - mod(beatInfo.beat, 1.);
    return speed * .25 * 0.87 * (time + .3 * timeAdjustment); // 0.87 keeps it from pausing at the same spot each cycle
}

vec2 distort(vec2 p)
{
    float theta  = atan(p.y, p.x);
    float radius = length(p);
    radius = pow(radius, 1.0+depth);
    p.x = radius * cos(theta);
    p.y = radius * sin(theta);
    return 0.5 * (p + 1.0);
}

vec4 pattern(vec2 p)
{
    vec2 m=mod(p.xy+p.x+p.y,2.)-1.;
    return vec4(length(m+p*0.1));
}

float hash(float n)
{
    return fract(sin(n)*29712.15073);
}

float noise(vec3 x, float y, float z)
{
    vec3 p=floor(x); vec3 f=fract(x);
    f=f*f*(3.0-2.0*f);
    float n=p.x+p.y*y+p.z*z;
    float r1=mix(mix(hash(n+0.0),hash(n+1.0),f.x),mix(hash(n+y),hash(n+y+1.0),f.x),f.y);
    float r2=mix(mix(hash(n+z),hash(n+z+1.0),f.x),mix(hash(n+y+z),hash(n+y+z+1.0),f.x),f.y);
    return mix(r1,r2,f.z);
}
void mainImage( out vec4 fragColor, in vec2 fragCoord ) {

    float RY = 0.0;	float RZ = 0.0;
    if (rxy <= 1.)			{	RY += 11.;	}
    else if (rxy <= 2.)		{	RY += 13.; 	}
    else if (rxy <= 3.)		{	RY += 17.; 	}
    else if (rxy <= 4.)		{	RY += 19.; 	}
    else if (rxy <= 5.)		{	RY += 23.; 	}
    else if (rxy <= 6.)		{	RY += 29.; 	}
    else if (rxy <= 8.)		{	RY += 31.; 	}
    else if (rxy <= 9.)		{	RY += 37.; 	}
    else if (rxy <= 10.)	{	RY += 41.; 	}
    else if (rxy <= 11.)	{	RY += 43.; 	}
    else if (rxy <= 12.)	{	RY += 47.; 	}
    else if (rxy <= 13.)	{	RY += 53.; 	}
    else if (rxy <= 14.)	{	RY += 59.; 	}
    else if (rxy <= 15.)	{	RY += 61.; 	}
    else if (rxy <= 16.)	{	RY += 67.; 	}
    if (rxz <= 1.)			{	RZ += 11.; 	}
    else if (rxz <= 2.)		{	RZ += 13.; 	}
    else if (rxz <= 3.)		{	RZ += 17.; 	}
    else if (rxz <= 4.)		{	RZ += 19.; 	}
    else if (rxz <= 5.)		{	RZ += 23.; 	}
    else if (rxz <= 6.)		{	RZ += 29.; 	}
    else if (rxz <= 8.)		{	RZ += 31.; 	}
    else if (rxz <= 9.)		{	RZ += 37.; 	}
    else if (rxz <= 10.)	{	RZ += 41.; 	}
    else if (rxz <= 11.)	{	RZ += 43.; 	}
    else if (rxz <= 12.)	{	RZ += 47.; 	}
    else if (rxz <= 13.)	{	RZ += 53.; 	}
    else if (rxz <= 14.)	{	RZ += 59.; 	}
    else if (rxz <= 15.)	{	RZ += 61.; 	}
    else if (rxz <= 16.)	{	RZ += 67.; 	}

    vec2 pos = ( fragCoord.xy / RENDERSIZE.xy * zoom )+center;
    float t = pulsedTime() * speed;
    float col = noise(pos.xyx + (t*rate),RY,RZ);
    vec4 c = pattern(distort(pos+col));
    c.xy = distort(c.xy);
    fragColor = vec4(c.x - col, sin(c.y) - col, cos(c.z), 1.0);

}