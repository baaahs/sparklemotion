// Blue Dream

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2      resolution;
uniform float     time;
// SPARKLEMOTION GADGET: Beat { "name": "beat" }
uniform float sm_beat;

float rand(vec2 n) {
    return fract(cos(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float noise(vec2 n) {
    const vec2 d = vec2(0.0, 1.0);
    vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));
    return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
}

float fbm(vec2 n) {
    float total = 0.0, amplitude = 2.0;
    for (int i = 0; i < 18; i++) {
        total += (noise(n) * amplitude);
        n += n;
        amplitude *= atan(0.4345);
    }
    return total;
}

void main() {
    const vec3 c1 = vec3(26.0/255.0, 111.0/255.0, 97.0/255.0);
    const vec3 c2 = vec3(73.0/255.0, 64.0/255.0, 181.4/255.0);
    const vec3 c3 = vec3(0.9, 0., 0.0);
    const vec3 c4 = vec3(64.0/255.0, 1.0/255.0, 114.4/255.0);
    const vec3 c5 = vec3(0.1);
    const vec3 c6 = vec3(0.3, 0.4, 0.2);

    vec2 p = abs(gl_FragCoord.xy) * (sin(sm_beat*2.*3.141)) / (resolution.xx);
    float q =abs(exp2(fbm(p - time * 0.08)));
    vec2 r = abs(vec2(fbm(p + q + time * 0.125 - p.x - p.y), fbm(p + q - time * 1.0)));
    vec3 c = mix(c1, c2, fbm(p + r)) + mix(c3, c4, r.x) - mix(c5, c6, r.y);


    gl_FragColor = abs(vec4((c )* sqrt(1.), 1.0));
    gl_FragColor.xyz *= 1.;
    gl_FragColor.w = 1.1;
}
