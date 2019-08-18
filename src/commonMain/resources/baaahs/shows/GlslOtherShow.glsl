// Devil's Playground

uniform float time;
uniform vec2 resolution;

#define PI 3.14159265359

void main(void)
{
	vec2 uv = gl_FragCoord.xy / resolution.xy;
	gl_FragColor = vec4(0);
    gl_FragColor.r = sin(88.*uv.x) + sin(55.*uv.y) + 0.5+2.*sin(time*2.);
}
