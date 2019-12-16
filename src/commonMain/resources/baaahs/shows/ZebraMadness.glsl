// Zebra Madness
// From http://glslsandbox.com/e#56511

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

vec4 circle(vec2 p, vec2 c)
{
	p-=c;
    if (p.x*p.x+p.y*p.y<0.001) return vec4(1.0,1.0,1.0,1.0);
    return vec4(0.0,0.0,0.0,0.0);
}


vec2 md(vec2 p) {
  p.x = mod(p.x,0.2);
  p.y = mod(p.y,0.2);
  return p;
}
vec4 map0(vec2 p, float z)
{
   p.x/=z;
   p.y/=z;
   p = md(p);
   return circle(p,vec2(0.1,0.1));	
}
vec4 map1(vec2 p,float d, float m) {
   d = mod(d,1.3);
   
   vec4 ca = map0(p,m*0.1+d);
   vec4 c0 = map0(p,m*0.3+d);
   vec4 c1 = map0(p,m*0.5+d);
   vec4 c2 = map0(p,m*0.7+d);
   vec4 c3 = map0(p,m*0.9+d);
   vec4 c4 = map0(p,m*1.1+d);
   vec4 c5 = map0(p,m*1.3+d);
   return ca+c0+c1+c2+c3+c4+c5;
}
vec4 map2(vec2 p, float d)
{
	vec4 c0 = map1(p,d,0.1);
	vec4 c1 = map1(p,d,0.2);
	vec4 c2 = map1(p,d,0.3);
	vec4 c3 = map1(p,d,0.4);
	vec4 c4 = map1(p,d,0.5);
	return c0+c1+c2+c3+c4;
}

void main( void ) {

	vec2 position = ( gl_FragCoord.xy / resolution.yy ) ;
	position.x-=0.6;
	position.y-=0.5;

	float color = 0.0;
	color += sin( position.x * cos( time / 15.0 ) * 80.0 ) + cos( position.y * cos( time / 15.0 ) * 10.0 );
	color += sin( position.y * sin( time / 10.0 ) * 40.0 ) + cos( position.x * sin( time / 25.0 ) * 40.0 );
	color += sin( position.x * sin( time / 5.0 ) * 10.0 ) + sin( position.y * sin( time / 35.0 ) * 80.0 );
	color *= sin( time / 10.0 ) * 0.5;

	//position = md(position);
    gl_FragColor = color * map2(position, time/1.0) + map2(position, (time - .5)/1.0) / 50.; //circle(position,vec2(0.1,0.1));
}
