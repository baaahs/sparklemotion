// Circular Dependency
// From http://glslsandbox.com/e#56705

/*
 * Original shader from: https://www.shadertoy.com/view/Wtf3zl
 */

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time; // @@Time
uniform vec2 center; // @@XyPad
uniform vec2 resolution; // @@Resolution

// --------[ Original ShaderToy begins here ]---------- //
void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord/resolution.xy;
    uv-=.5;
    uv.x*= resolution.x/resolution.y;
    uv -= center;

    vec3 col = vec3(0.5);
    float d =length(uv)*20.;
    float a = atan(uv.y, uv.x);
    col.r = smoothstep(0.1, .2, abs(mod(d+time, 2.)-1.));
	col.g = col.r*floor(mod(d*.5+.5+time*.5, 2.));
    float f = smoothstep(-.1, .1,sin(a*3.+(sin(time*.5)*2.)*d-time));
    col.rg = mix(1.-col.rg, col.rg, f);

    // Output to screen
    fragColor = vec4(col,1.0);
}

