//Based on https://www.youtube.com/watch?v=3CycKKJiwis

float random(vec2 par){
    return fract(sin(dot(par.xy,vec2(12.9898,78.233))) * 43758.5453);
}

vec2 random2(vec2 par){
    float rand = random(par);
    return vec2(rand, random(par+rand));
}

// @param fragCoord uv-coordinate
// @return color
vec4 upstreamColor(vec2 fragCoord);

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = fragCoord/iResolution.xy;

    //The ratio of the width and height of the screen
    float widthHeightRatio = iResolution.x/iResolution.y;

    float t = iTime * 0.01;
    float dist = 0.0;
    float layers = 16.0;
    float scale = 32.0;
    float depth;
    float phase;
    float rotationAngle = iTime * -0.01;

    vec2 offset;
    vec2 local_uv;
    vec2 index;
    vec2 pos;
    vec2 seed;
    vec2 centre = vec2(0.5, 0.5);

    mat2 rotation = mat2(cos(rotationAngle), -sin(rotationAngle),
    sin(rotationAngle),  cos(rotationAngle));

    for(float i = 0.0; i < layers; i++){
        depth = fract(i/layers + t);

        //Move centre in a circle depending on the depth of the layer
        centre.x = 0.5 + 0.1 * cos(t) * depth;
        centre.y = 0.5 + 0.1 * sin(t) * depth;

        //Get uv from the fragment coordinates, rotation and depth
        uv = centre-fragCoord/iResolution.xy;
        uv.y /= widthHeightRatio;
        uv *= rotation;
        uv *= mix(scale, 0.0, depth);

        //The local cell
        index = floor(uv);

        //Local cell seed;
        seed = 20.0 * i + index;

        //The local cell coordinates
        local_uv = fract(i + uv) - 0.5;

        //Get a random position for the local cell
        pos = 1.8 * (random2(seed) - 0.5);

        //Get a random phase
        phase = 128.0 * random(seed);

        //Get distance to the generated point, add fading to distant points
        //Add the distance to the sum
        dist += pow(abs(1.0-length(local_uv-pos)), 50.0 + 20.0 * sin(phase + 8.0 * iTime))
        * min(1.0, depth*2.0);

    }
    fragColor = upstreamColor(fragCoord) + vec4(vec3(dist),1.0) * 10.;
}