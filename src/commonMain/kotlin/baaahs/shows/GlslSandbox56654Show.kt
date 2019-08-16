package baaahs.shows

object GlslSandbox56654Show : GlslShow("GlslSandbox 56654") {

    override val program = """
#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

#define N 12.0
#define FADESPEED 2.0

#define EPSILON (0.001)
#define EQUAL1(a,b) (((a - EPSILON) < b) && ((a + EPSILON) > b))
#define EQUAL3(a,b) (EQUAL1(a.x, b.x) && EQUAL1(a.y, b.y) && EQUAL1(a.z, b.z))

vec2 rec2hex(vec2 rec)
{
	float temp = floor(rec.x + sqrt(3.0) * rec.y + 1.0);
	float q = floor((floor(2.0 * rec.x + 1.0) + temp) / 3.0);
	float r = floor((temp + floor(-rec.x + sqrt(3.0) * rec.y + 1.0)) / 3.0);
	return vec2(q,r);
}

vec3 axial_to_cube(vec2 hex)
{
	return vec3(hex.x, hex.y, -hex.x+hex.y);
}

float hex_length(vec3 hex) {
    return floor((abs(hex.x) + abs(hex.y) + abs(hex.z)) / 2.0);
}

float hex_distance(vec3 a, vec3 b) {
    return hex_length(a - b);
}

vec3 hex_round(vec3 h) {
    float q = floor(h.x);
    float r = floor(h.y);
    float s = floor(h.z);
    float q_diff = abs(q - h.x);
    float r_diff = abs(r - h.y);
    float s_diff = abs(s - h.z);
    if ((q_diff > r_diff) && (q_diff > s_diff)) {
        q = -r - s;
    } else if (r_diff > s_diff) {
        r = -q - s;
    } else {
        s = -q - r;
    }
    return vec3 (q, r, s);
}

vec2 rotate(in vec2 point, in float rads)
{
	float cs = cos(rads);
	float sn = sin(rads);
	return point * mat2(cs, -sn, sn, cs);
}

vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
  return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)
  {
  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                     -0.577350269189626,  // -1.0 + 2.0 * C.x
                      0.024390243902439); // 1.0 / 41.0
// First corner
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

// Other corners
  vec2 i1;
  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
  //i1.y = 1.0 - i1.x;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  // x0 = x0 - 0.0 + 0.0 * C.xx ;
  // x1 = x0 - i1 + 1.0 * C.xx ;
  // x2 = x0 - 1.0 + 2.0 * C.xx ;
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

// Permutations
  i = mod289(i); // Avoid truncation effects in permutation
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
		+ i.x + vec3(0.0, i1.x, 1.0 ));

  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;

// Gradients: 41 points uniformly over a line, mapped onto a diamond.
// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

// Normalise gradients implicitly by scaling m
// Approximation of: m *= inversesqrt( a0*a0 + h*h );
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

// Compute final noise value at P
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

vec2 rand_loc(highp float i,  highp float t)
{
	highp float offset = i * 11.0;
	highp float seed = t + (offset * 200.0);

	return vec2(snoise(vec2(seed, 11.0)),
		    snoise(vec2(seed, 88.765433)));

}

void main( void )
{
	vec2 position = ((gl_FragCoord.xy / resolution.xy) * 2.0) - 1.0;
	position.y *= resolution.y/resolution.x;

	float n = 7.0;
	vec2 pos = position * n;

	vec2 hpos = rec2hex(pos);
	vec3 orig_cube = axial_to_cube(hpos);
	vec3 orig_icube = hex_round(orig_cube);

	vec2 rhpos = rotate(hpos, time);
	vec3 cube = axial_to_cube(rhpos);
	vec3 h = abs(cube);
	float d = length(cube);

	vec3 color = vec3(0.0);

	color.rb = 1.0 - h.xz/n;
	float gfade = (cos(time) + 1.0) / 2.0;
	color.g = gfade * (1.0 - d/n);

	float bright = 0.0;

	for (highp float i = 0.0; i < 1.0; i += (1.0/N)) {
		float t = time + i;
		float fadetime = t * FADESPEED;
		float steptime = floor(fadetime);
		float stepfract = fract(fadetime);
		float stepprog = sin(stepfract * 3.14159265);
		stepprog = stepprog * stepprog;


		vec2 loc = rand_loc(i, steptime);
		vec2 loc_axial = rec2hex(loc * n);
		vec3 loc_hex = axial_to_cube(loc_axial);
		vec3 loc_ihex = hex_round(loc_hex);
		if (EQUAL3(loc_ihex, orig_icube)) {
			bright += stepprog;
		}
	}
	float cbright = clamp(bright, 0.0, 1.0);

#define R_SIZE  3.7
#define R_MOD   1.0
#define R_SPEED 1.666
#define R_WIDE  1.3
#define R_MARGIN (R_WIDE / 2.0)

	float r = R_SIZE + (R_MOD * sin(time * R_SPEED));
	if ((d > (r - R_MARGIN)) && (d < (r + R_MARGIN))) {
		vec3 grey = vec3((color.r + color.g + color.b) / 3.0);
		grey *= 1.1;
		color = mix(grey, color, gfade);
		//color.rb /= 3.0;
		//color = vec3(0.0);
	}

	color += vec3(cbright);

	gl_FragColor = vec4(color, 1.0);
}
"""

}
