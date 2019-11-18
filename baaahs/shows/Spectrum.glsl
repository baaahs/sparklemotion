// Spectrum

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// SPARKLEMOTION GADGET: Slider {name: "Scale", initialValue: 1.0, minValue: 0.0, maxValue: 2.0}
uniform float scale;

void main(void)
{
    float mag = texture(sm_soundAnalysis, vec2(gl_FragCoord.y, pow(gl_FragCoord.x - .25, scale))).r;
    float magNow = texture(sm_soundAnalysis, vec2(gl_FragCoord.y, 0)).r;
    gl_FragColor = vec4(mag, mag, magNow, 1.0);

}

