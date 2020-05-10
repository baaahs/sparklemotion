// Orange Snowflake
// Based on http://glslsandbox.com/e#61105.0

/*
 * Original shader from: https://www.shadertoy.com/view/wl3XW8
 */

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// shadertoy emulation
float iTime = 0.0;
#define iResolution resolution

// --------[ Original ShaderToy begins here ]---------- //
// Code by Flopine
// Thanks to wsmind, leon, XT95, lsdlive, lamogui, Coyhot, Alkama and YX for teaching me
// Thanks LJ for giving me the love of shadercoding :3

// Thanks to the Cookie Collective, which build a cozy and safe environment for me
// and other to sprout :)  https://twitter.com/CookieDemoparty

#define hr vec2(1., sqrt(3.))
#define PI 3.141592
#define TAU (2.*PI)
#define time (iTime*0.5)

float hash21 (vec2 x)
{return fract(sin(dot(x,vec2(12.4,18.4)))*1245.4);}

mat2 rot (float a)
{return mat2 (cos(a),sin(a),-sin(a),cos(a));}

void mo (inout vec2 p, vec2 d)
{
    p = abs(p)-d;
    if (p.y>p.x) p = p.yx;
}

float stmin (float a, float b, float k, float n)
{
    float st = k/n;
    float u = b-k;
    return min(min(a,b), 0.5*(u+a+abs(mod(u-a+st,2.*st)-st)));
}

float hd (vec2 uv)
{
    uv = abs(uv);
    return max(uv.x, dot(uv, normalize(hr)));
}

vec4 hgrid (vec2 uv,float detail)
{
    uv *= detail;
    vec2 ga = mod(uv,hr)-hr*0.5;
    vec2 gb = mod(uv-hr*0.5,hr)-hr*0.5;
    vec2 guv = (dot(ga,ga)< dot(gb,gb))? ga: gb;

    vec2 gid = uv-guv;

    guv.y = 0.5-hd(guv);

    return vec4(guv,gid);
}

float hexf (vec2 uv)
{
    float det = 3.;
    float speed = 0.5;
    float d = 0.;
    for (float i=0.; i<3.; i++)
    {
        float ratio = i/5.;
        uv *= rot(TAU/(5.));
        uv = (mod(i,2.) == 0.) ? vec2(uv.x+iTime*speed,uv.y) : vec2(uv.x,uv.y+iTime*speed);
        d += step(hgrid(uv, det).y,0.03);
        speed -= 0.1;
        det ++;
    }
    return d;
}

float box (vec3 p, vec3 c)
{
    vec3 q = abs(p)-c;
    return min(0.,max(q.x,max(q.y,q.z))) + length(max(q,0.));
}

float fractal (vec3 p)
{
    float size = 1.;
    float d = box(p,vec3(size));
    for (float i=0.; i<5.; i++)
    {
        float ratio = i/5.;
        p.yz *= rot(time);
        mo(p.xz, vec2(2.+ratio));
        mo(p.xy, vec2(0.5+ratio));
        p.xy *= rot(time+ratio);
        size -= ratio*1.5;
        d= stmin(d,box(p,vec3(size)),1., 4.);
    }
    return d;
}

float g1 = 0.;
float SDF (vec3 p)
{
    float d = fractal(p);
    g1 += 0.1/(0.1+d*d);
    return d;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = (2.*fragCoord-iResolution.xy)/iResolution.y;

    float mask = 1.0;//step(0.3, abs(sin(length(uv)-PI*time))+0.01);
    float fx = 0.0;//clamp(mix(1.-hexf(uv), hexf(uv), mask),0.,1.);

    float dither = hash21(uv);

    vec3 ro = vec3(0.,0.,-10.),
    p = ro,
    rd = normalize(vec3(uv,1.)),
    col = vec3(0.);

    float shad = 0.;

    for (float i=0.; i<64.; i++)
    {
        float d = SDF(p);
        if (d<0.01)
        {
            shad = i/64.;
            break;
        }
        d *= 0.7+dither*0.1;
        p += d*rd;
    }

    col = vec3(2.,length(uv*0.5),0.1)*g1*0.2;
    col *= (1.-fx);

    // Output to screen
    fragColor = vec4(col,1.0);
}
    // --------[ Original ShaderToy ends here ]---------- //

    #undef time

void main(void)
{
    iTime = time * 0.5;
    mainImage(gl_FragColor, gl_FragCoord.xy);
}