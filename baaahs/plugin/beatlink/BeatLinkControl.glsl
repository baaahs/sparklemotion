// BeatLinkControlShader

precision mediump float;

#define PI 3.14159

uniform float time;
uniform vec2 resolution;

uniform float brightness; // @pass-through

uniform vec4 pixCoords; // @@baaahs.Core:RasterCoordinate @type raster-coordinate
uniform vec2 pixDimens; // @@baaahs.Core:PreviewResolution @type preview-resolution

struct RawBeatInfo {
    float measureStartTime;
    float beatIntervalMs;
    float bpm;
    float beatsPerMeasure;
    float confidence;
    float trackStartTime;
};
uniform RawBeatInfo rawBeatInfo; // @@baaahs.BeatLink:RawBeatInfo

float secPerBeat = rawBeatInfo.beatIntervalMs / 1000.;

const vec2 beatsBorderPx = vec2(6.);

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

vec4 drawBeats(vec2 pos) {
    float pX = pos.x * rawBeatInfo.beatsPerMeasure;
    float pY = pos.y;

    float linearBeatPower = 1. - mod(pX, 1.);
    float linearBeatIntensity = beatIntensity_(linearBeatPower);

    vec3 color = vec3(0.);
    float o = 1.;

    // Beat-tracking bar, wide channel.
    float nowBeatWideBandMarker = step(abs(pX - nowPower - beatInMeasure), .11);

    // Beat-tracking bar, red slider LED.
    float wideBandHotLine = nowBeatWideBandMarker * step(abs(pY - nowBeatIntensity), .025);
    color += o * wideBandHotLine * nowBeatIndicatorColor.rgb;
    o -= o * wideBandHotLine;

    // Beat-tracking bar, gooey indicator center.
    float nowBeatXBand = step(abs(pX - nowPower - beatInMeasure), .04);
    float nowBeatIntensityMarker = nowBeatXBand * nowBeatIntensityColor.a;
    color += o * nowBeatIntensityMarker * nowBeatIntensityColor.rgb * step(pY, nowBeatIntensity);
    o -= o * nowBeatIntensityMarker;

    color += o * nowBeatWideBandColor.rgb * nowBeatWideBandMarker;
    o -= o * nowBeatWideBandMarker * nowBeatWideBandColor.a;

    // Sine wave-ish intensity preview.
    float beatIntensityBand = 1. - smoothstep(1.0 - distance(1.-linearBeatIntensity + pY - .5, .5), 1.0, 0.98);
    color += o * beatIntensityBand * beatIntensityColor;
    o -= o * beatIntensityBand;

    // Beat sawtooth.
    float beatMountain = step(pY, linearBeatPower);
    float beatMountainHighlight = not(nowBeatIntensity) * eq(beatInMeasure, floor(pX));
    color += o * beatMountain * (beatPowerColor + beatMountainHighlight);
    o -= o * beatMountain;

    // Background.
    color += o * background(pos);
    return vec4(color, 1.);
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

    // Draw border.
    float border = 1. - rect(beatsBorderPx - 1., pixDimens - beatsBorderPx + 1., pixPos);
    color += border * borderColor * nowBeatIntensity;
    o -= o * border;
    vec2 paddedPos = (pos * pixDimens - beatsBorderPx) / (pixDimens - beatsBorderPx * 2.);

    // Draw beats.
    color += o * drawBeats(paddedPos).rgb;
    color = mix(color.rrr * .125 + color.ggg * .25 + color.bbb * .125, color, rawBeatInfo.confidence);

    gl_FragColor = vec4(color * brightness, 1.);
}