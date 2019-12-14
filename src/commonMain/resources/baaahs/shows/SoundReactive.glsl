// Sound Reactive

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// SPARKLEMOTION PLUGIN: SoundAnalysis {maxFrequency: 200}
uniform float lows;

// SPARKLEMOTION PLUGIN: SoundAnalysis {minFrequency: 200, maxFrequency: 800}
uniform float mids;

// SPARKLEMOTION PLUGIN: SoundAnalysis {minFrequency: 800}
uniform float highs;

void main(void)
{
    gl_FragColor = vec4(lows, mids, highs, 1.0);
}

