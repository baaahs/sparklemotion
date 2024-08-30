// BeatLinkControlShader

precision mediump float;

#define PI 3.14159

uniform float time;
uniform vec2 resolution;

struct SoundAnalysis {
    int bucketCount;
    int sampleHistoryCount;
    sampler2D buckets;
    float maxMagnitude;
};
uniform SoundAnalysis soundAnalysis; // @@baaahs.SoundAnalysis:SoundAnalysis

uniform bool sonicRunwayMode; // @pass-through

const vec2 eqBottomLeft = vec2(.1, .1);
const vec2 eqTopRight = vec2(.9, .9);
const vec2 eqDimen = eqTopRight - eqBottomLeft;

const vec3 backgroundColor = vec3(0.1, .6, 0.);
const vec3 borderColor = vec3(.4, 0., 0.);

float eq(float a, float b) {
    if (a == b) {
        return 1.;
    } else {
        return 0.;
    }
}

float not(float v) {
    return 1. - v;
}

float rect(vec2 bottomLeft, vec2 topRight, vec2 pos) {
    if (step(pos, eqBottomLeft) == vec2(0.) && step(eqTopRight, pos) == vec2(0.)) {
        return 1.;
    } else {
        return 0.;
    }
}

vec3 background(vec2 pos) {
    return backgroundColor - vec3(not(pos.y) * .5);
}

vec4 drawEq(vec2 pos) {
    vec3 color = vec3(0.);

    vec2 txPos = vec2(pos.y, pow(1. - pos.x, 2.));
    float magnitude = texture(soundAnalysis.buckets, txPos).r;
    vec3 eqColor = vec3(
        magnitude,
        (magnitude - .333) * 3. / 2.,
        (magnitude - .666) * 3.
    );
    color += clamp(eqColor, 0., 1.);

    return vec4(color, 1.);
}

void main(void) {
    vec2 pos = gl_FragCoord.xy / resolution.xy;
    vec3 color = vec3(0.);
    float o = 1.;

    // Draw border.
    float border = 1. - rect(eqBottomLeft, eqTopRight, pos);
    color += border * borderColor * soundAnalysis.maxMagnitude;
    o -= o * border;

    // Draw eq.
    color += o * drawEq((pos - eqBottomLeft) / eqDimen).rgb;

    if (sonicRunwayMode) {
        color = vec3(0., 0., 1.);
    }
    gl_FragColor = vec4(color, 1.);
}
