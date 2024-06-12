// Checkerboard

uniform float checkerboardSize = 10.0; // @@Slider min=.001 max=1 default=.125

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec2 pos = floor(fragCoord / checkerboardSize);
    vec3 patternMask = vec3(mod(pos.x + mod(pos.y, 2.0), 2.0));
    fragColor = vec4(patternMask.xy, 1., 1.);
}