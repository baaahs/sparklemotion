// BeatLinkControlShader

precision mediump float;

#define PI 3.14159

uniform float time;
uniform vec2 resolution;

uniform vec4 pixCoords; // @@baaahs.Core:RasterCoordinate @type raster-coordinate
uniform vec2 pixDimens; // @@baaahs.Core:PreviewResolution @type preview-resolution

uniform float trackElapsedTime; // @pass-through
uniform float trackLength; // @pass-through
uniform sampler2D waveform; // @pass-through

float trackViewWidthInSeconds = 10.;
float trackHistory = .5; // Show half a second of historical track waveform.
float fadeQuickness = 5.;

vec3 drawWaveform(vec2 pos) {
    float y = pos.y * 2. - 1.;
    float ampY = abs(y);

    float trackHistoryOffset = trackHistory / trackViewWidthInSeconds;

    // center horizontal dashed line
    if (ampY < 2. / pixDimens.y && mod(pos.x * 10., 1.) < .5)
        return vec3(0., 0., .8);

    // "now" vertical line
    float currentPositionInTrack = trackElapsedTime / trackLength - trackHistoryOffset / trackLength;
    if (abs(currentPositionInTrack - pos.x) < 1. / pixDimens.x)
        return vec3(.8, 0., 0.);

    // waveform
    vec4 sampleData = texture(waveform, vec2(pos.x, 0.));
    if (ampY <= sampleData.a) {
        float alpha = exp(-1. * fadeQuickness * (sampleData.a - ampY));
        return vec3(sampleData.rgb * alpha);
    }

    // background
    return vec3(0.);
}

void main(void) {
    vec2 pos = gl_FragCoord.xy / resolution.xy;

    // Draw waveform.
    gl_FragColor = vec4(drawWaveform(pos), 1.);
}