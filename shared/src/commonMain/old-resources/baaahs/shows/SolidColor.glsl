// Solid Color

uniform float time;
uniform vec2 resolution;
uniform vec4 color; // @@ColorPicker default=#3300ff
uniform float brightness; // @@Slider default=.75
uniform float sparkliness; // @@Slider default=.02 max=.5

float random (vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123 + time);
}

void main() {
    gl_FragColor = color * brightness
        + ((sparkliness < random(gl_FragCoord.xy / resolution.xy)) ? vec4(0.) : vec4(1.));
}