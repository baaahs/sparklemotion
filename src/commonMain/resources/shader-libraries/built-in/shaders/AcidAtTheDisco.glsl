// Acid at the Disco
// Modified from: https://www.shadertoy.com/view/4sfXRB

uniform float time; // @@Time
uniform vec2 resolution; // @@Resolution
uniform float speed; // @@Slider default=3.0 min=1.0 max=5.0
uniform float pulsiness; // @@Slider default=3.0 min=1.0 max=5.0

struct BeatInfo {
    float beat;
    float bpm;
    float intensity;
    float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo

float beatIntegral() {
    float t = mod(beatInfo.beat, 1.);
    float POWER = 4.; // Adjusts sharpnett of the curve
    float OFFSET = 0.0; // Adjusts future-offset of curve. OFFSET=0.5 means the steepest part happens between beats.
    return 1. - pow(1. - mod(t + OFFSET, 1.0), POWER);
}

float pulsedTime() {
    float timeAdjustment = beatIntegral() - mod(beatInfo.beat, 1.);
    return speed * .25 * 0.87 * (time + .1 * pulsiness * timeAdjustment); // 0.87 keeps it from pausing at the same spot each cycle
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord.xy / resolution.xy;
    float t = pulsedTime();
    float depth = sin(uv.y*2.0+sin(t)*1.5+1.0+sin(uv.x*3.0+t*1.2))*cos(uv.y*2.0+t)+sin((uv.x*3.0+t));
    float texey = (uv.x-0.5);
    float xband = sin(sqrt(uv.y/uv.y)*16.0/(depth)+t*3.0);
    float final = (
    sin(texey/abs(depth)*32.0+t*16.0+sin(uv.y*uv.x*32.0*sin(depth*3.0)))*(depth)*xband
    );


    fragColor = vec4(-final*abs(sin(t)),(-final*sin(t)*2.0),(final),1.0)*1.5;
}