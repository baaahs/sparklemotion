// Flat Projection

// Use this to project pixels onto a model

struct ModelInfo {
    vec3 center;
    vec3 extents;
};
uniform ModelInfo modelInfo;

// @return uv-coordinate
// @param pixelLocation xyz-coordinate
vec2 main(vec3 pixelLocation) {
    vec3 start = modelInfo.center - modelInfo.extents / 2.;
    vec3 rel = (pixelLocation - start) / modelInfo.extents;
    return rel.xy;
}
