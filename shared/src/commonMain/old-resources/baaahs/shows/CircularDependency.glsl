// Circular Dependency
// From http://glslsandbox.com/e#56705

/*
 * Original shader from: https://www.shadertoy.com/view/Wtf3zl
 */

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;
uniform vec2 mouse;

// shadertoy emulation
#define iTime time
#define iResolution resolution

// --------[ Original ShaderToy begins here ]---------- //
void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord/iResolution.xy;
    uv-=.5;
    uv.x*= iResolution.x/iResolution.y;

    vec3 col = vec3(0.5);
    float d =length(uv)*20.;
    float a = atan(uv.y, uv.x);
    col.r = smoothstep(0.1, .2, abs(mod(d+iTime, 2.)-1.));
	col.g = col.r*floor(mod(d*.5+.5+iTime*.5, 2.));
    float f = smoothstep(-.1, .1,sin(a*3.+(sin(iTime*.5)*2.)*d-iTime));
    col.rg = mix(1.-col.rg, col.rg, f);

    // Output to screen
    fragColor = vec4(col,1.0);
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}
