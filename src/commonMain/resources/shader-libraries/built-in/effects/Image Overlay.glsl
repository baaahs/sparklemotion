// Image Overlay

vec4 image(vec2 uv); // @@Image

// @return color
// @param inColor color
vec4 main(vec4 inColor) {
    vec4 i = image(gl_FragCoord);
    return vec4(mix(inColor.rgb, i.rgb, i.a), 1.0);
}