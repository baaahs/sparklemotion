uniform float brightness; // @@Slider min=0 max=1.25 default=1

// @return color
// @param inColor color
vec4 main(vec4 inColor) {
    vec4 clampedColor = clamp(inColor, 0., 1.);
    return vec4(clampedColor.rgb * brightness, clampedColor.a);
}