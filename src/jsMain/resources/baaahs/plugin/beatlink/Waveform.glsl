// BeatLinkControlShader

precision mediump float;

#define PI 3.14159

uniform float time;
uniform vec2 resolution;

uniform vec4 pixCoords; // @@baaahs.Core:RasterCoordinate @type raster-coordinate
uniform vec2 pixDimens; // @@baaahs.Core:PreviewResolution @type preview-resolution

struct RawBeatInfo {
    float measureStartTime;
    float beatIntervalMs;
    float bpm;
    float beatsPerMeasure;
    float confidence;
};
uniform RawBeatInfo rawBeatInfo; // @@baaahs.BeatLink:RawBeatInfo

uniform float trackElapsedTime; // @pass-through
uniform float trackLength; // @pass-through
uniform sampler2D waveform; // @pass-through

float secPerBeat = rawBeatInfo.beatIntervalMs / 1000.;

const vec2 beatsBorderPx = vec2(0.);

const vec3 backgroundColor = vec3(.1, .1, .4);
const vec3 beatPowerColor = vec3(0., .0, .5);
const vec3 beatIntensityColor = vec3(1., .5, .0);

const vec3 nowBeatIndicatorColor = vec3(1., .0, .0);
const vec4 nowBeatIntensityColor = vec4(0., 1., .0, .6);
const vec4 nowBeatWideBandColor = vec4(.8, .6, .0, .8);
const vec3 borderColor = vec3(.0, .4, .0);

// TODO: This function should be a switchable strategy.
float beatIntensity_(float power) {
    return clamp(
    pow(sin((power * 1. + .55) * PI), 4.) * 1.25 + .0,
    .0, 1.
    );
}

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
    if (step(pos, bottomLeft) == vec2(0.) && step(topRight, pos) == vec2(0.)) {
        return 1.;
    } else {
        return 0.;
    }
}

vec3 background(vec2 pos) {
    return backgroundColor - vec3(not(pos.y) * .5);
}

float timeInMeasure;
float beatInMeasure;
float timeInBeat;
float nowPower;
float nowBeatIntensity;

vec4 drawWaveform(vec2 pos) {
    float y = pos.y * 2. - 1.;
    float ampY = abs(y);

    float trackViewWidthInSeconds = 10.;
    float trackHistory = .5; // Show half a second of historical track waveform.

    float trackHistoryOffset = trackHistory / trackViewWidthInSeconds;

    // center horizontal dashed line
    if (ampY < 2. / pixDimens.y && mod(pos.x * 20., 1.) < .75)
        return vec4(0., 1., 0., .8);

    // "now" vertical line
    if (abs(pos.x - trackHistoryOffset) < 1. / pixDimens.x)
    return vec4(1., 0., 0., .8);

    // waveform
    float fadeQuickness = 5.;
    float currentPositionInTrack = trackElapsedTime / trackLength - trackHistoryOffset / trackLength;
    vec4 sampleData = texture(waveform, vec2(currentPositionInTrack + pos.x / trackLength * trackViewWidthInSeconds, 0.));
//    vec4 sampleData = texture(waveform, vec2(pos.x, 0.));

    if (ampY <= sampleData.a) {
        float alpha = exp(-1. * fadeQuickness * (sampleData.a - ampY));
        // return vec4(sampleData.rgb, sampleData.a - ampY < .1 ? 1. : .5);
        return vec4(sampleData.rgb, alpha);
    }

    // background
    return vec4(0.);
}

void main(void) {
    vec2 pos = gl_FragCoord.xy / resolution.xy;
    vec2 pixPos = pixCoords.xy;

    timeInMeasure = mod((time - rawBeatInfo.measureStartTime) / secPerBeat, rawBeatInfo.beatsPerMeasure);
    beatInMeasure = floor(timeInMeasure);
    timeInBeat = mod(timeInMeasure, 1.);
    nowPower = timeInBeat;
    nowBeatIntensity = beatIntensity_(nowPower);

    vec3 color = vec3(0.);
    float o = 1.;

    // Draw waveform.
    vec4 waveformColor = drawWaveform(pos);
    color = mix(color, waveformColor.rgb, waveformColor.a * o);
    o -= o * waveformColor.a * o;

    gl_FragColor = vec4(color, 1.);
}