// Færie Pulse
// From http://glslsandbox.com/e#46723

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;

float rand(int seed, float ray) {
  return mod(sin(float(seed)*1.0+ray*1.0)*1.0, 1.0);
}

mat2 rotate2d(float _angle) {
  return mat2(cos(_angle), -sin(_angle), sin(_angle), cos(_angle));
}

void main() {
  vec2 uv = gl_FragCoord.xy / resolution.xy;
  vec3 uv3 = vec3(sin(time*0.1), uv);
  vec2 center = resolution / max(resolution.x, resolution.y) * 0.5;

  //float a = rand(5234, uv.x)*6.2+time*5.0*(rand(2534, uv.y)-rand(3545, uv.x));
  float a = distance(uv, center)*rand(5234, uv.x)-6.2*(rand(2534, uv.y));

  float ac = cos(time*0.1-a);
  float as = sin(time*0.1+a);
  uv3 *= mat3(
  ac, as, 0.0, -as, ac, 0.0, 0.0, 0.0, 1.0);
  uv.x = uv3.x;
  uv.y = uv3.y;
  //uv = rotate2d(time*0.1)*uv;
  uv = mod(uv, 0.5) * 2.0;
  //uv.x = cos(time-a);
  //uv.y = sin(time+a);
  gl_FragColor = vec4(uv, cos(distance(uv, center)), 1.0);
}
