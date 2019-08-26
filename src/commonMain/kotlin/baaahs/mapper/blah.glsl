
    #version 330
    uniform sampler2D sm_uvCoords;
    uniform float sm_uScale;
    uniform float sm_vScale;
    uniform float sm_startOfMeasure;
    uniform float sm_beat;

    out vec4 sm_fragColor;

    // Acid at the Disco
// From: https://www.shadertoy.com/view/4sfXRB

// shadertoy emulation
#define iTime time
#define iResolution resolution

uniform float time;
uniform vec2 resolution;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord.xy / iResolution.xy;
    float time = iTime*0.75;
    float depth = sin(uv.y*2.0+sin(time)*1.5+1.0+sin(uv.x*3.0+time*1.2))*cos(uv.y*2.0+time)+sin((uv.x*3.0+time));
    float texey = (uv.x-0.5);
    float xband = sin(sqrt(uv.y/uv.y)*16.0/(depth)+time*(3.0));
    float final = (
    sin(texey/abs(depth)*32.0+time*16.0+sin(uv.y*uv.x*32.0*sin(depth*3.0)))*(depth)*xband
    );


    fragColor = vec4(final*abs(sin(time)),(-final*sin(time)*2.0),(sm_beat-final),1.0)*1.5;
}

void main() {
    mainImage(sm_fragColor, sm_pixelCoord);
}



    // Coming in, `gl_FragCoord` is a vec2 where `x` and `y` correspond to positions in `sm_uvCoords`.
    // We look up the `u` and `v` coordinates (which should be floats `[0..1]` in the mapping space) and
    // pass them to the shader's original `main()` method.
    void main(void) {
        int uvX = int(gl_FragCoord.x);
        int uvY = int(gl_FragCoord.y);

        vec2 pixelCoord = vec2(
            texelFetch(sm_uvCoords, ivec2(uvX * 2, uvY), 0).r * sm_uScale,    // u
            texelFetch(sm_uvCoords, ivec2(uvX * 2 + 1, uvY), 0).r * sm_vScale // v
        );

        sm_main(pixelCoord);
    }

