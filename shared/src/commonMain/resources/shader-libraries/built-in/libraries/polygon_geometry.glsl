// Polygon drawing library
// Ben Bartlett

#define PI 3.1415926535897932384626433832795


#define N 5

// Test function
vec2 VERTS[5] =
    vec2[5](
    vec2(0., 0.),
    vec2(0., 1.),
    vec2(.5, .5),
    vec2(1., 1.),
    vec2(1., 0.)
);

// Determines whether a point is inside a 2D polygon given by a list of points.
// Unfortunately GLSL can't handle variably sized arrays so you'll need to change N and make a new function
// each time you want to use this
bool pointInPoly(vec2 point, float scale, vec2 offset, vec2 vertices[N]){
    int i, j;
    bool c = false;
    vec2 pt = (point - offset)/scale;
    for (i = 0, j = N-1; i < N; j = i++) {
        if ( ((vertices[i].y > pt.y) != (vertices[j].y > pt.y)) &&
        (pt.x < (vertices[j].x-vertices[i].x) * (pt.y-vertices[i].y) / (vertices[j].y-vertices[i].y) + vertices[i].x) )
        c = !c;
    }
    return c;
}

float sdfCircle(vec2 uv, float r, vec2 offset) {
    float x = uv.x - offset.x;
    float y = uv.y - offset.y;

    return length(vec2(x, y)) - r;
}

float sdfSquare(vec2 uv, float size, vec2 offset) {
    float x = uv.x - offset.x;
    float y = uv.y - offset.y;

    return max(abs(x), abs(y)) - size;
}

// Example of how you could use this
vec3 drawScene(vec2 uv) {
    vec3 col = vec3(1);
    float circle = sdfCircle(uv, 0.1, vec2(0, 0));
    float square = sdfSquare(uv, 0.07, vec2(0.1, 0));

    float poly = -1.0 * float(pointInPoly(uv, .1, vec2(-.3,-.5), VERTS));

    col = mix(vec3(0, 0, 1), col, step(0., circle));
    col = mix(vec3(1, 0, 0), col, step(0., square));
    col = mix(vec3(0, 1, 0), col, step(0., poly));

    return col;
}
