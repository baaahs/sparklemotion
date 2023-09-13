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
    float trackStartTime;
};
uniform RawBeatInfo rawBeatInfo; // @@baaahs.BeatLink:RawBeatInfo

struct PlayerState {
    float trackStartTime;
    float trackLength;
};

struct PlayerData {
    PlayerState players[4];
};
uniform PlayerData playerData; // @@baaahs.BeatLink:PlayerData

uniform sampler2D waveforms[4]; // @@baaahs.BeatLink:Waveforms

float secPerBeat = rawBeatInfo.beatIntervalMs / 1000.;

const vec2 beatsBorderPx = vec2(5.);

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

vec4 drawOneWaveformz(sampler2D waveform, int playerNumber, vec2 pos, float offset, float scale) {
    float trackStartTime = playerData.players[playerNumber].trackStartTime;
    float trackLength = playerData.players[playerNumber].trackLength;

    return vec4(0.);
}
vec4 drawOneWaveform(sampler2D waveform, PlayerState playerState, vec2 pos, float offset, float scale) {
    float trackStartTime = playerState.trackStartTime;
    pos = vec2(pos.x, (pos.y - offset) * scale);
    float y = pos.y * 2. - 1.;

    vec4 sampleData = texture(waveform, pos);
    float ampY = abs(y);

    // center dashed line
    if (ampY < .05 && mod(pos.x * 20., 1.) < .75)
    return vec4(0., 1., 0., .8);

    // black hairline border
    if (ampY > .97)
    return vec4(0., 0., 0., .75);

    // waveform
    if (ampY <= sampleData.a) {
        float fadeQuickness = 5.;
        float alpha = exp(-1. * fadeQuickness * (sampleData.a - ampY));
        // return vec4(sampleData.rgb, sampleData.a - ampY < .1 ? 1. : .5);
        return vec4(sampleData.rgb, alpha);
    }

    // background
    return vec4(0.);
}

vec4 drawWaveform(vec2 pos) {
    vec4 waveformValue;
    if (pos.y < .25) {
        waveformValue = drawOneWaveform(waveforms[0], playerData.players[0], pos, 0., 4.);
    } else if (pos.y < .5) {
        waveformValue = drawOneWaveform(waveforms[1], playerData.players[1], pos, .25, 4.);
    } else if (pos.y < .75) {
        waveformValue = drawOneWaveform(waveforms[2], playerData.players[2], pos, .5, 4.);
    } else {
        waveformValue = drawOneWaveform(waveforms[3], playerData.players[3], pos, .75, 4.);
    }
    return waveformValue;
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
    float border = 1. - rect(beatsBorderPx, pixDimens - beatsBorderPx, pixPos);
    color += border * borderColor * nowBeatIntensity;
    o -= o * border;
    vec2 paddedPos = (pos * pixDimens - beatsBorderPx) / (pixDimens - beatsBorderPx * 2.);

    // Draw waveform.
    vec4 waveformColor = drawWaveform(paddedPos);
    color = mix(color, waveformColor.rgb, waveformColor.a * o);
    o -= o * waveformColor.a * o;

    // Draw beats.
    color += o * drawBeats(paddedPos).rgb;
    color = mix(color.rrr * .125 + color.ggg * .25 + color.bbb * .125, color, rawBeatInfo.confidence);

    gl_FragColor = vec4(color, 1.);
}