// Strings
// from https://www.shadertoy.com/view/WldXRS

#define LINE_FUNKINESS 23.23


float Hash21(vec2 p) {
    p = fract(p*vec2(123.34, 456.21));
    p += dot(p, p+45.32);
    return fract(p.x*p.y);
}

vec3 makeLine(vec2 uv, vec2 r_uv, float row_id, float scale, float i, float n)
{
    float direction = sign(n * 2. - 1.);

    vec2 current_uv = vec2(r_uv);

    current_uv.y += sin(r_uv.x + iTime * direction * 2.23 * n + row_id * 2.3623) * LINE_FUNKINESS * n * (sin(iTime + n * 80.932) * 0.5 + 0.5) * (sin(iTime) * 0.5 + 0.5);


    vec3 current_col = 0.5 + 0.5*cos(iTime+uv.xyx+vec3(0,2,4) + row_id * 23.61232);
    return scale / pow(abs(current_uv.y - i * 2.), (1.5 + n*2.) * (1. + sin(iTime + 3.14159*0.5) * 0.5)) * current_col;
}



void mainImage( out vec4 fragColor, in vec2 fragCoord )
{

    float rows = 25.0;
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = fragCoord/iResolution.xy;

    uv.y += iTime * 0.05;

    float a = iTime * 0.1;// + length(uv) * 3.141592653589 * sin(iTime * 0.12316);
    float s = sin(a);
    float c = cos(a);

    uv *= mat2(c, -s, s, c);


    uv.x *= iResolution.x / iResolution.y;;

    uv.y *= rows;

    float row_id = floor(uv.y);
    vec2 r_uv = vec2(uv.x, fract(uv.y));
    r_uv = r_uv * 2.0 - 1.0;

    // Time varying pixel color
    //vec3 col = 0.5 + 0.5*cos(iTime+uv.xyx+vec3(0,2,4)) * c;


    float mag = 0.005 * rows;
    vec3 col = vec3(0.0);
    for(float i = -rows; i <= rows; i++)
    {
        float current_row_id = row_id + i;
        float n = Hash21(vec2(current_row_id, mag));
        col += makeLine(uv, r_uv, current_row_id, mag + sin(iTime * 0.5 + n * 6.1412) * 0.1, i, n);
    }

    for(float i = -rows; i <= rows; i++)
    {
        float current_row_id = row_id + i;
        float n = Hash21(vec2(current_row_id, mag * 0.1));
        col += makeLine(uv, r_uv, current_row_id, mag * 0.1 + sin(iTime * 0.5 + n * 14.6875) * 0.01, i, n);
    }

    for(float i = -rows; i <= rows; i++)
    {
        float current_row_id = row_id + i;
        float n = Hash21(vec2(current_row_id, mag * 0.05));
        col += makeLine(uv, r_uv, current_row_id, mag * 0.05 + sin(iTime * 0.5 + n * 23.78563) * 0.01, i, n);
    }





    float power = 2.0;
    col = vec3(pow(col.x, power), pow(col.y, power), pow(col.z, power));

    // Output to screen
    fragColor = vec4(col,1.0);
}