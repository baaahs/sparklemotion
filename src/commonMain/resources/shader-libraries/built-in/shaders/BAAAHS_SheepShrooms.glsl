// Sheep disco shader thing idk what to call this one
// BAAAHS 2023

// Uses code from https://www.shadertoy.com/view/DlB3WG
// Combined with own code from https://www.shadertoy.com/view/7dVBWV
// Made on too little sleep lol

//#define resolution  iResolution
//#define time        iTime
#define PI          3.141592654
#define PI_2        (0.5*PI)
#define TAU         (2.0*PI)
#define ROT(a)      mat2(cos(a), sin(a), -sin(a), cos(a))

#define BEATMOVE 1

// Copy-paste this to add a mock beatInfo object to shadertoy for testing purposes
#define BPM 115.0/2.
// These will need to be Find-Replaced from beatInfo_beat -> beatInfo.beat
#define beatInfo_beat mod(time * (BPM*2. /60.), 4.)
#define beatInfo_bpm BPM*4.
#define beatInfo_intensity .5*(smoothstep(1., 0., mod(beatInfo_beat, 1.) / 0.4) + .5*smoothstep(1., 0., (1. - mod(beatInfo_beat, 1.)) / 0.2))
#define beatInfo_confidence 1.0

uniform float time; // @@Time
uniform vec2 resolution; // @@Resolution

struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
    // New stuff for BAAAHS 2024:
    // Facilitates seamless transitions
    float faderlevel;
    float fader2level;
    float fader3level;
    float fader4level;
    float crossfaderABlevel;
    float filter1level;
    float filter2level;
    float filter3level;
    float filter4level;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo

const float FREQ_RANGE = 64.0;
const float RADIUS = 0.4;
const float BRIGHTNESS = 0.2;
const float SPEED = 0.4;


//uniform float planeDist; // @@Slider default=0.8 min=0.5 max=0.9
//uniform int furthest; // @@Slider default=6 min=4 max=20

const float planeDist = 1.0-0.8;
const int   furthest  = 6;
const int   fadeFrom  = max(furthest-4, 0);
const float fadeDist  = planeDist*float(furthest - fadeFrom);

const float overSample  = 4.0;
const float ringDistance= 0.075*overSample/4.0;
const float noOfRings   = 20.0*4.0/overSample;
const float glowFactor  = 0.06;

/* Minified BAAAHS logo code: use pointInSheep(vec2 point, float scale, vec2 offset) to determine if a pixel is in the sheep logo. */ float COORD_SCALE = 0.03; vec2 HEART[187] = vec2[187](vec2(-1.408e1, 2.96), vec2(-1.39e1, 3.56), vec2(-1.326e1, 3.86), vec2(-1.37e1, 4.4), vec2(-1.36e1, 5.08), vec2(-1.322e1, 5.4), vec2(-1.268e1, 5.46), vec2(-13, 5.92), vec2(-1.292e1, 6.52), vec2(-1.242e1, 6.92), vec2(-1.174e1, 6.84), vec2(-1.198e1, 7.4), vec2(-1.174e1, 7.98), vec2(-1.126e1, 8.22), vec2(-1.054e1, 8.02), vec2(-1.058e1, 8.66), vec2(-1.012e1, 9.14), vec2(-9.6, 9.18), vec2(-9.1, 8.88), vec2(-9.08, 9.42), vec2(-8.62, 9.86), vec2(-8, 9.86), vec2(-7.54, 9.42), vec2(-7.4, 9.94), vec2(-6.88, 1.028e1), vec2(-6.22, 1.014e1), vec2(-5.9, 9.56), vec2(-5.46, 1.01e1), vec2(-4.78, 1.014e1), vec2(-4.42, 9.88), vec2(-4.22, 9.32), vec2(-3.86, 9.8), vec2(-3.2, 9.88), vec2(-2.74, 9.54), vec2(-2.6, 8.88), vec2(-2.02, 9.24), vec2(-1.38, 9.04), vec2(-1.12, 8.6), vec2(-1.14, 8.06), vec2(-0.52, 8.28), vec2(0, 8.02), vec2(0.58, 8.16), vec2(1.04, 7.96), vec2(1, 8.5), vec2(1.28, 8.96), vec2(1.98, 9.14), vec2(2.54, 8.78), vec2(2.52, 9.3), vec2(2.98, 9.8), vec2(3.6, 9.82), vec2(4.12, 9.3), vec2(4.22, 9.72), vec2(4.62, 1.008e1), vec2(5.32, 1.006e1), vec2(5.78, 9.52), vec2(6.1, 1.014e1), vec2(6.74, 1.028e1), vec2(7.24, 9.98), vec2(7.4, 9.44), vec2(7.98, 9.92), vec2(8.64, 9.82), vec2(8.98, 9.4), vec2(8.98, 8.88), vec2(9.5, 9.24), vec2(1.02e1, 9.1), vec2(1.052e1, 8.58), vec2(1.046e1, 8.04), vec2(1.11e1, 8.3), vec2(1.166e1, 8.04), vec2(1.188e1, 7.38), vec2(1.166e1, 6.92), vec2(1.236e1, 7), vec2(1.284e1, 6.62), vec2(1.294e1, 6.06), vec2(1.268e1, 5.54), vec2(1.326e1, 5.48), vec2(1.368e1, 4.92), vec2(1.364e1, 4.44), vec2(1.326e1, 3.98), vec2(1.378e1, 3.82), vec2(1.41e1, 3.24), vec2(1.394e1, 2.66), vec2(1.34e1, 2.36), vec2(1.386e1, 2.08), vec2(1.408e1, 1.62), vec2(1.388e1, 0.94), vec2(1.32e1, 0.68), vec2(1.36e1, 0.16), vec2(1.356e1, -0.42), vec2(1.322e1, -0.8), vec2(1.262e1, -0.92), vec2(1.302e1, -1.38), vec2(1.294e1, -2.06), vec2(1.244e1, -2.44), vec2(1.188e1, -2.38), vec2(1.212e1, -2.92), vec2(1.198e1, -3.46), vec2(1.152e1, -3.8), vec2(1.094e1, -3.74), vec2(1.114e1, -4.44), vec2(1.08e1, -5.02), vec2(1.036e1, -5.18), vec2(9.86, -5.06), vec2(1.006e1, -5.66), vec2(9.84, -6.16), vec2(9.3, -6.42), vec2(8.72, -6.22), vec2(8.82, -6.92), vec2(8.46, -7.42), vec2(7.88, -7.54), vec2(7.48, -7.38), vec2(7.62, -8), vec2(7.34, -8.48), vec2(6.74, -8.68), vec2(6.26, -8.46), vec2(6.36, -9.14), vec2(5.88, -9.7), vec2(5.34, -9.74), vec2(4.94, -9.48), vec2(4.98, -1.016e1), vec2(4.64, -1.058e1), vec2(4.18, -1.072e1), vec2(3.56, -1.046e1), vec2(3.64, -1.102e1), vec2(3.3, -1.152e1), vec2(2.76, -1.166e1), vec2(2.22, -1.14e1), vec2(2.28, -1.198e1), vec2(1.92, -1.248e1), vec2(1.34, -1.26e1), vec2(0.86, -1.232e1), vec2(0.9, -13), vec2(0.74, -1.328e1), vec2(0.28, -1.354e1), vec2(-0.24, -1.35e1), vec2(-0.6, -1.324e1), vec2(-0.76, -1.242e1), vec2(-1.22, -1.266e1), vec2(-1.84, -1.25e1), vec2(-2.14, -1.204e1), vec2(-2.08, -1.146e1), vec2(-2.62, -1.172e1), vec2(-3.24, -1.152e1), vec2(-3.52, -11), vec2(-3.42, -1.052e1), vec2(-3.9, -1.076e1), vec2(-4.54, -1.062e1), vec2(-4.88, -1.006e1), vec2(-4.8, -9.54), vec2(-5.32, -9.82), vec2(-5.94, -9.64), vec2(-6.24, -9.1), vec2(-6.1, -8.5), vec2(-6.54, -8.74), vec2(-7.18, -8.58), vec2(-7.5, -8.02), vec2(-7.38, -7.44), vec2(-7.82, -7.62), vec2(-8.36, -7.48), vec2(-8.72, -6.88), vec2(-8.56, -6.32), vec2(-9.08, -6.5), vec2(-9.56, -6.36), vec2(-9.94, -5.72), vec2(-9.74, -5.14), vec2(-1.034e1, -5.26), vec2(-1.086e1, -4.96), vec2(-1.104e1, -4.34), vec2(-1.082e1, -3.82), vec2(-1.128e1, -3.92), vec2(-1.168e1, -3.76), vec2(-1.202e1, -3.1), vec2(-1.18e1, -2.48), vec2(-1.242e1, -2.54), vec2(-1.294e1, -2.02), vec2(-1.294e1, -1.46), vec2(-1.256e1, -1.04), vec2(-1.314e1, -0.92), vec2(-1.352e1, -0.46), vec2(-1.352e1, 8.0e-2), vec2(-1.31e1, 0.54), vec2(-1.382e1, 0.78), vec2(-1.406e1, 1.5), vec2(-1.382e1, 1.98), vec2(-1.336e1, 2.22), vec2(-1.386e1, 2.46), vec2(-1.406e1, 2.94)); vec2 HEART_MIN_XY = vec2(-70.5, -67.9); vec2 HEART_MAX_XY = vec2(70.6, 51.6); vec2 FACE[26] = vec2[26](vec2(-5.24, 2.5), vec2(-5.18, 3.56), vec2(-5.04, 3.84), vec2(-4.8, 3.88), vec2(-3.58, 3.3), vec2(-1.88, 2.12), vec2(2.16, 2.12), vec2(4.02, 3.68), vec2(4.78, 4.04), vec2(5.18, 4.02), vec2(5.38, 3.64), vec2(5.44, 2.72), vec2(5.12, 0), vec2(2.84, -7.52), vec2(2.7, -8.46), vec2(2.36, -9.22), vec2(1.86, -9.8), vec2(1.14, -1.026e1), vec2(0.46, -1.046e1), vec2(-0.36, -1.044e1), vec2(-1.28, -1.008e1), vec2(-1.88, -9.58), vec2(-2.38, -8.8), vec2(-2.64, -7.5), vec2(-4.76, -0.72), vec2(-5.22, 2.48)); vec2 MOUTH[35] = vec2[35](vec2(-1.66, -6.76), vec2(-1.56, -6.52), vec2(-1.3, -6.58), vec2(-0.32, -7.72), vec2(0.2, -7.86), vec2(0.58, -7.68), vec2(1.4, -6.66), vec2(1.62, -6.52), vec2(1.84, -6.58), vec2(1.82, -6.84), vec2(1.52, -6.98), vec2(1.22, -7.28), vec2(0.3, -8.66), vec2(0.3, -9.18), vec2(0.4, -9.28), vec2(1.14, -9.04), vec2(1.66, -8.64), vec2(1.8, -8.68), vec2(1.78, -8.82), vec2(1.44, -9.2), vec2(0.98, -9.52), vec2(0.42, -9.78), vec2(2.0e-2, -9.84), vec2(-0.86, -9.46), vec2(-1.54, -8.82), vec2(-1.56, -8.68), vec2(-1.44, -8.64), vec2(-1, -8.98), vec2(-0.18, -9.28), vec2(-8.0e-2, -9.16), vec2(-8.0e-2, -8.74), vec2(-0.14, -8.5), vec2(-0.8, -7.54), vec2(-1.26, -7.02), vec2(-1.64, -6.76)); vec2 EYE_LEFT[10] = vec2[10](vec2(-4.6, 2.4), vec2(-4.6, 2.72), vec2(-4.46, 2.98), vec2(-4.14, 3.02), vec2(-3.82, 2.8), vec2(-3.48, 2.14), vec2(-3.54, 1.6), vec2(-4, 1.6), vec2(-4.26, 1.72), vec2(-4.58, 2.38)); vec2 EYE_RIGHT[10] = vec2[10](vec2(3.64, 1.8), vec2(3.72, 2.34), vec2(4, 2.8), vec2(4.32, 3.02), vec2(4.68, 2.94), vec2(4.78, 2.42), vec2(4.48, 1.76), vec2(4.2, 1.6), vec2(3.7, 1.6), vec2(3.66, 1.78)); vec2 EAR_LEFT[9] = vec2[9](vec2(-1.092e1, 6.08), vec2(-1.084e1, 6.26), vec2(-1.058e1, 6.3), vec2(-7.02, 6.24), vec2(-6.94, 4.48), vec2(-7.14, 4.38), vec2(-7.84, 4.42), vec2(-1.078e1, 5.88), vec2(-1.09e1, 6.06)); vec2 EAR_RIGHT[10] = vec2[10](vec2(7.22, 4.48), vec2(7.42, 4.38), vec2(8.12, 4.42), vec2(1.106e1, 5.88), vec2(1.12e1, 6.14), vec2(1.112e1, 6.26), vec2(1.086e1, 6.3), vec2(7.36, 6.28), vec2(7.26, 6.18), vec2(7.22, 4.5));  bool pointInHeart(vec2 point, float scale, vec2 offset){ int len = 187;    int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE);    for (i = 0, j = len-1; i < len; j = i++) { if (((HEART[i].y > p.y) != (HEART[j].y > p.y)) && (p.x < (HEART[j].x-HEART[i].x) * (p.y-HEART[i].y) / (HEART[j].y-HEART[i].y) + HEART[i].x)) c = !c; }    return c; } bool pointInFace(vec2 point, float scale, vec2 offset){ int len = 26;    int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE);    for (i = 0, j = len-1; i < len; j = i++) { if (((FACE[i].y > p.y) != (FACE[j].y > p.y)) && (p.x < (FACE[j].x-FACE[i].x) * (p.y-FACE[i].y) / (FACE[j].y-FACE[i].y) + FACE[i].x)) c = !c; }    return c; } bool pointInMouth(vec2 point, float scale, vec2 offset){ int len = 35;    int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE);    for (i = 0, j = len-1; i < len; j = i++) { if (((MOUTH[i].y > p.y) != (MOUTH[j].y > p.y)) && (p.x < (MOUTH[j].x-MOUTH[i].x) * (p.y-MOUTH[i].y) / (MOUTH[j].y-MOUTH[i].y) + MOUTH[i].x)) c = !c; }    return c; } bool pointInEyeLeft(vec2 point, float scale, vec2 offset){ int len = 10;    int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE);    for (i = 0, j = len-1; i < len; j = i++) { if (((EYE_LEFT[i].y > p.y) != (EYE_LEFT[j].y > p.y)) && (p.x < (EYE_LEFT[j].x-EYE_LEFT[i].x) * (p.y-EYE_LEFT[i].y) / (EYE_LEFT[j].y-EYE_LEFT[i].y) + EYE_LEFT[i].x)) c = !c; }    return c; } bool pointInEyeRight(vec2 point, float scale, vec2 offset){ int len = 10;    int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE);    for (i = 0, j = len-1; i < len; j = i++) { if (((EYE_RIGHT[i].y > p.y) != (EYE_RIGHT[j].y > p.y)) && (p.x < (EYE_RIGHT[j].x-EYE_RIGHT[i].x) * (p.y-EYE_RIGHT[i].y) / (EYE_RIGHT[j].y-EYE_RIGHT[i].y) + EYE_RIGHT[i].x)) c = !c; }    return c; } bool pointInEarLeft(vec2 point, float scale, vec2 offset){ int len = 9;    int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE);    for (i = 0, j = len-1; i < len; j = i++) { if (((EAR_LEFT[i].y > p.y) != (EAR_LEFT[j].y > p.y)) && (p.x < (EAR_LEFT[j].x-EAR_LEFT[i].x) * (p.y-EAR_LEFT[i].y) / (EAR_LEFT[j].y-EAR_LEFT[i].y) + EAR_LEFT[i].x)) c = !c; }    return c; } bool pointInEarRight(vec2 point, float scale, vec2 offset){ int len = 10;    int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE);    for (i = 0, j = len-1; i < len; j = i++) { if (((EAR_RIGHT[i].y > p.y) != (EAR_RIGHT[j].y > p.y)) && (p.x < (EAR_RIGHT[j].x-EAR_RIGHT[i].x) * (p.y-EAR_RIGHT[i].y) / (EAR_RIGHT[j].y-EAR_RIGHT[i].y) + EAR_RIGHT[i].x)) c = !c; }    return c; }  bool pointInSheep(vec2 point, float scale, vec2 offset) { vec2 p = (point - offset)/(scale * COORD_SCALE);    if (p.x > HEART_MAX_XY.x || p.y > HEART_MAX_XY.y || p.x < HEART_MIN_XY.x || p.y < HEART_MIN_XY.y) { return false; }    bool inHeart = pointInHeart(point, scale, offset);    bool inFace = pointInFace(point, scale, offset);    bool inMouth = pointInMouth(point, scale, offset);    bool inEyeLeft = pointInEyeLeft(point, scale, offset);    bool inEyeRight = pointInEyeRight(point, scale, offset);    bool inEarLeft = pointInEarLeft(point, scale, offset);    bool inEarRight = pointInEarRight(point, scale, offset);    return inHeart && (!(inFace || inEarLeft || inEarRight) || (inMouth || inEyeLeft || inEyeRight)); }


//convert HSV to RGB
vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float luma(vec3 color) {
    return dot(color, vec3(0.299, 0.587, 0.114));
}

float getfrequency(float x) {
    return .2 + .3 * beatInfo.intensity;//texture(soundAnalysis.buckets, vec2(floor(x * FREQ_RANGE + 1.0) / FREQ_RANGE, 0.25)).x + 0.06;
}

float getfrequency_smooth(float x) {
    float index = floor(x * FREQ_RANGE) / FREQ_RANGE;
    float next = floor(x * FREQ_RANGE + 1.0) / FREQ_RANGE;
    return mix(getfrequency(index), getfrequency(next), smoothstep(0.0, 1.0, fract(x * FREQ_RANGE)));
}

float getfrequency_blend(float x) {
    return mix(getfrequency(x), getfrequency_smooth(x), 0.5);
}

vec4 alphaBlend(vec4 back, vec4 front) {
    float w = front.w + back.w*(1.0-front.w);
    vec3 xyz = (front.xyz*front.w + back.xyz*back.w*(1.0-front.w))/w;
    return w > 0.0 ? vec4(xyz, w) : vec4(0.0);
}

vec3 alphaBlend(vec3 back, vec4 front) {
    return mix(back, front.xyz, front.w);
}

float tanh_approx(float x) {
    float x2 = x*x;
    return clamp(x*(27.0 + x2)/(27.0+9.0*x2), -1.0, 1.0);
}

float hash(float co) {
    return fract(sin(co*12.9898) * 13758.5453);
}

vec3 offset(float z) {
    float a = z;
    vec2 p = -0.1*vec2(1.2, .6)*(vec2(cos(a), sin(a*sqrt(2.0))) + vec2(cos(a*sqrt(0.75)), sin(a*sqrt(0.5))));
    return vec3(p, z);
}

vec3 doffset(float z) {
    float eps = 0.05;
    return 0.5*(offset(z + eps) - offset(z - eps))/(2.0*eps);
}

vec3 ddoffset(float z) {
    float eps = 0.05;
    return 0.5*(doffset(z + eps) - doffset(z - eps))/(2.0*eps);
}

vec3 skyColor(vec3 ro, vec3 rd) {
    return vec3(0.0);
}

// License: MIT OR CC-BY-NC-4.0, author: mercury, found: https://mercury.sexy/hg_sdf/
float mod1(inout float p, float size) {
    float halfsize = size*0.5;
    float c = floor((p + halfsize)/size);
    p = mod(p + halfsize, size) - halfsize;
    return c;
}

// License: MIT, author: Pascal Gilcher, found: https://www.shadertoy.com/view/flSXRV
float atan_approx(float y, float x) {
    float cosatan2 = x / (abs(x) + abs(y));
    float t = PI_2 - cosatan2 * PI_2;
    return y < 0.0 ? -t : t;
}


vec2 toPolar(vec2 p) {
    return vec2(length(p), atan_approx(p.y, p.x));
}

vec3 glow(vec2 pp, float h) {
    float hh = fract(h*8677.0);
    float b = TAU*h+0.5*time*(hh > 0.5 ? 1.0 : -1.0);
    float a = pp.y+b;
    float d = max(abs(pp.x)-0.001, 0.00125);
    return
    (smoothstep(0.667*ringDistance, 0.2*ringDistance, d)
    * smoothstep(0.1, 1.0, cos(a))
    * glowFactor
    * ringDistance
    / d
    )
    * (cos(a+b+vec3(0, 1, 2))+vec3(1.0))
    ;
}

vec3 glowRings(vec2 p, float hh) {
    vec2 pp = toPolar(p);
    vec3 col = vec3(0.0);
    float h = 1.0;
    const float nr = 1.0/overSample;

    for (float i = 0.0; i < overSample; ++i) {
        vec2 ipp = pp;
        ipp.x -= ringDistance*(nr*i);
        float rn = mod1(ipp.x, ringDistance);
        h = hash(rn+123.0*i);
        col += glow(ipp, h)*step(rn, noOfRings);
    }

    col += (0.01*vec3(1.0, 0.25, 0.0))/length(p);

    return col;
}

vec4 plane(vec3 ro, vec3 rd, vec3 pp, vec3 off, float aa, float n) {
    float h = hash(n+123.4);

    vec3 hn;
    vec2 p = (pp-off*vec3(1.0, 1.0, 0.0)).xy;
    float l = length(p);
    float fade = smoothstep(0.1, 0.15, l);
    if (fade < 0.1) return vec4(0.0);
    vec4 col = vec4(0.0);

    col.xyz = glowRings(p*mix(0.5, 4.0, h), h);
    float i = max(max(col.x, col.y), col.z);

    col.w = (tanh_approx(0.5+max((i), 0.0))*fade);
    return col;
}


vec3 glow_sheep(vec2 pp, float h) {
    float hh = fract(h*8677.0);
    float b = TAU*h+0.5*time*(hh > 0.5 ? 1.0 : -1.0);
    float a = pp.y+b;
    float d = max(abs(pp.x)-0.001, 0.00125);
    return
    (smoothstep(0.667*ringDistance, 0.2*ringDistance, d)
    * 1.0//* smoothstep(0.1, 1.0, cos(a))
    * glowFactor
    * ringDistance
    / d
    )
    * (cos(a+b+vec3(0, 1, 2))+vec3(1.0))
    ;
}

vec3 drawSheep(vec2 fragment, float scale) {
    float dist = length(fragment);
    float ring = 1.0 / abs(dist - scale);

    float sheepScale = 1.9 * scale;
    vec2 sheepXY = vec2(0.0, 0.00);

    //float b = pointInSheep(fragment, sheepScale, sheepXY) ? BRIGHTNESS : 0.1 * BRIGHTNESS; // dist < scale ? BRIGHTNESS * 0.3 : BRIGHTNESS;
    float b = dist < scale ? BRIGHTNESS * 0.3 : BRIGHTNESS;


    if (pointInSheep(fragment, sheepScale, sheepXY)) { b = 50. * b; }

    vec3 col = vec3(0.0);

    float angle = atan(fragment.x, fragment.y);
    //col += hsv2rgb(vec3((angle + iTime * 0.25) / (PI * 2.0), 1.0, 1.0)) * ring * b;

    col += glow_sheep(.1*fragment, 123.4) * ring * b;

    float frequency = max(getfrequency_blend(abs(angle / PI)) - 0.02, 0.0);
    col *= frequency;

    return col;
}


vec3 color(vec3 ww, vec3 uu, vec3 vv, vec3 ro, vec2 p) {
    float lp = length(p);
    vec2 np = p + 1.0/resolution.xy;
    const float rdd_per   = 37.0;

    float inner_rad=2.75;
    float outer_rad=1.0;
    float rdd = inner_rad + outer_rad*pow(lp, 1.5)*tanh_approx(lp+0.9*0.5*(cos(rdd_per*p.x)+1.0)*0.5*(cos(rdd_per*p.y)+1.0));
    //float rdd = 5.0;
    vec3 rd = normalize(p.x*uu + p.y*vv + rdd*ww);
    vec3 nrd = normalize(np.x*uu + np.y*vv + rdd*ww);

    float nz = floor(ro.z / planeDist);
    float nz_sheep = ro.z / planeDist;

    vec3 skyCol = skyColor(ro, rd);

    vec4 acol = vec4(0.0);

    const float cutOff = 0.95;
    bool cutOut = false;

    float maxpd = 0.0;

    // Steps from nearest to furthest plane and accumulates the color
    for (int i = 1; i <= furthest; ++i) {
        float pz = planeDist*nz + planeDist*float(i);
        float pz_sheep = planeDist*nz_sheep + planeDist * float(i);

        float pd = (pz - ro.z)/rd.z;
        float pd_sheep = (pz_sheep - ro.z) / rd.z;

        if (pd > 0.0 && acol.w < cutOff) {
            vec3 pp = ro + rd*pd;
            maxpd = pd;
            vec3 npp = ro + nrd*pd;

            float aa = 3.0*length(pp - npp);

            vec3 off = offset(pp.z);

            vec4 pcol = plane(ro, rd, pp, off, aa, nz+float(i));


            float nz = pp.z-ro.z;
            float fadeIn = smoothstep(planeDist*float(furthest), planeDist*float(fadeFrom), nz);
            float fadeOut = smoothstep(0.0, planeDist*0.1, nz);
            pcol.w *= fadeOut*fadeIn;
            pcol = clamp(pcol, 0.0, 1.0);

            /*if (i == furthest) {
                vec3 pp_sheep = ro + rd*pd_sheep;
                vec3 npp_sheep = ro + nrd*pd_sheep;
                vec3 off_sheep = offset(pp_sheep.z);
                vec2 p_sheep = (pp_sheep-off_sheep*vec3(1.0, 1.0, 0.0)).xy;
                pcol += vec4(drawSheep(3.*p_sheep, RADIUS), .4);
            }*/

            acol = alphaBlend(pcol, acol);
        } else {
            cutOut = true;
            acol.w = acol.w > cutOff ? 1.0 : acol.w;
            break;
        }

    }

    vec3 col = alphaBlend(skyCol, acol);

    float pz_sheep = planeDist*nz_sheep + planeDist * float(furthest);
    float pd_sheep = (pz_sheep - ro.z) / rd.z;
    vec3 pp_sheep = ro + rd*pd_sheep;
    vec3 npp_sheep = ro + nrd*pd_sheep;
    vec3 off_sheep = offset(pp_sheep.z);
    vec2 p_sheep = (pp_sheep-off_sheep*vec3(1.0, 1.0, 0.0)).xy;

    float sheep_scale=0.6;
    vec2 sheep_xy = p_sheep / sheep_scale;

    float logoRadius = RADIUS + 0.05 * min((beatInfo.intensity), 1.);
    float radiusFalloff = 4.;
    float dR = max(length(p_sheep) - logoRadius - 0.1, 0.);
    //uv0 /= (1. + exp(-radiusFalloff*dR) * logoRadius / LOGO_RADIUS);

    if (length(sheep_xy) < 1.2 * logoRadius) {
        col *= (1. + 2.*beatInfo.intensity);
    }
    if (length(sheep_xy) < logoRadius) {
        col *= .05;
    }
    col += drawSheep(sheep_xy, logoRadius);

    col *= (.95 + .1*beatInfo.intensity);

    // To debug cutouts due to transparency
    //  col += cutOut ? vec3(1.0, -1.0, 0.0) : vec3(0.0);
    return col;
}

vec3 effect(vec2 p, vec2 q) {
    float tm  = planeDist*time*BPM/60.0;
    vec3 ro   = offset(tm);
    vec3 dro  = doffset(tm);
    vec3 ddro = ddoffset(tm);

    vec3 ww = normalize(dro);
    vec3 uu = normalize(cross(normalize(vec3(0.0, 1.0, 0.0)+ddro), ww));
    vec3 vv = cross(ww, uu);

    vec3 col = color(ww, uu, vv, ro, p);

    // Random color tweaks
    col -= 0.075*vec3(2.0, 3.0, 1.0);
    col *= sqrt(2.0);
    col = clamp(col, 0.0, 1.0);
    col = sqrt(col);
    return col;
}


void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec2 q = fragCoord/resolution.xy;
    vec2 p = -1. + 2. * q;
    p.x *= resolution.x/resolution.y;

    vec3 col = effect(p, q);
    // vec3 sheepCol = drawSheep(p, RADIUS);
    fragColor = vec4(col, 1.0);
}
