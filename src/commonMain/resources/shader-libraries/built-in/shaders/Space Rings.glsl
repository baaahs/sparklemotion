// Space Rings

#define time iTime*1.25
#define p0 0.5, 0.5, 0.5,  0.5, 0.5, 0.5,  1.0, 1.0, 1.0,  0.0, 0.33, 0.67

const float numParticles = 25.;
const float numRings = 5.;
const float offsetMult = 30.;
const float tau = 6.23813;

vec3 palette( in float t, in float a0, in float a1, in float a2, in float b0, in float b1, in float b2,
in float c0, in float c1, in float c2,in float d0, in float d1, in float d2)
{
    return vec3(a0,a1,a2) + vec3(b0,b1,b2)*cos( tau*(vec3(c0,c1,c2)*t+vec3(d0,d1,d2)) );
}

vec3 particleColor(vec2 uv, float radius, float offset, float periodOffset)
{
    vec3 color = palette(.4 + offset / 4., p0);
    uv /= pow(periodOffset, .75) * sin(periodOffset * iTime) + sin(periodOffset + iTime);
    vec2 pos = vec2(cos(offset * offsetMult + time + periodOffset),
    sin(offset * offsetMult + time * 5. + periodOffset * tau));

    float dist = radius / distance(uv, pos);
    return color * pow(dist, 2.) * 1.75;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = (2. * fragCoord - iResolution.xy) / iResolution.y;
    uv *= 3.45;

    fragColor = vec4(0., 0., 0., 1.);

    for (float n = 0.; n <= numRings; n++)
    {
        for (float i = 0.; i <= numParticles; i++) {
            fragColor.rgb += particleColor(uv, .03, i / numParticles, n / 2.);
        }
    }
}