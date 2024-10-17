// GLSL Hue Test Pattern

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

void main(void)
{

    gl_FragColor = vec4(gl_FragCoord.xy / resolution, 0.0, 1.0);

}

