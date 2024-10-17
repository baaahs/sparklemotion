// Colorgasm
// From: https://www.shadertoy.com/view/4dffRs

/*
others did lab color space better than THIS:
https://www.shadertoy.com/results?query=lab

https://en.wikipedia.org/wiki/Lab_color_space#CIELAB
[HUNTER lab] and [CIE lab lch] both include CIExyz

[HUNTER lab] uses square roots pow(x, 1./2.)
[CIElab lch] uses cubic  roots pow(x, 1./3.)

this is just
https://www.shadertoy.com/view/Mdlfzf
made a bit smaller
*/

vec3 rgb2xyz (in vec3 rgb) {
    float r = rgb.r;
    float g = rgb.g;
    float b = rgb.b;
    r = r > 0.04045 ? pow(((r + 0.055) / 1.055), 2.4) : (r / 12.92);
    g = g > 0.04045 ? pow(((g + 0.055) / 1.055), 2.4) : (g / 12.92);
    b = b > 0.04045 ? pow(((b + 0.055) / 1.055), 2.4) : (b / 12.92);
    float x = (r * 0.4124) + (g * 0.3576) + (b * 0.1805);
    float y = (r * 0.2126) + (g * 0.7152) + (b * 0.0722);
    float z = (r * 0.0193) + (g * 0.1192) + (b * 0.9505);
    //i think theres a big error hee, but it is never used so it doesnt occur.
    vec3 xyz = vec3(
    (r * 0.4124) + (g * 0.3576) + (b * 0.1805) * 100.0,
    (r * 0.2126) + (g * 0.7152) + (b * 0.0722) * 100.0,
    (r * 0.0193) + (g * 0.1192) + (b * 0.9505) * 100.0);
    return(xyz);}

vec3 xyz2lab (in vec3 xyz) {
    float x = xyz.x / 95.047;
    float y = xyz.y / 100.0;
    float z = xyz.z / 108.883;
    x = x > 0.008856 ? pow(x, 1.0 / 3.0) : (7.787 * x) + (16.0 / 116.0);
    y = y > 0.008856 ? pow(y, 1.0 / 3.0) : (7.787 * y) + (16.0 / 116.0);
    z = z > 0.008856 ? pow(z, 1.0 / 3.0) : (7.787 * z) + (16.0 / 116.0);
    vec3 lab = vec3((116.0 * y) - 16.0, 500.0 * (x - y), 200.0 * (y - z));
    return(lab);}

vec3 rgb2lab(in vec3 rgb){return xyz2lab(rgb2xyz(rgb));}

float add(vec3 a){return a.x+a.y+a.z;}
    #define satt(a,b) clamp(a,b(0),b(1))
float sat(float a){return satt(a,float);}
vec3 sat(vec3 a){return satt(a,vec3);}
    //vec4 sat(vec4 a){return satt(a,vec4);}
    #define x2r(r) b.r=mix(p.r,b.r* 12.92,step(b.r,b1))
vec3 xyz2rgb(in vec3 a){a/=100.;const float b1=.0031308/12.92;vec3 b,p;
    b.r=add(a*vec3(3.2406,-1.5372,-.4986));
    b.g=add(a*vec3(-.9689,1.8758,.0415));
    b.b=add(a*vec3(.0557,-.2040,1.0570));//color matrix
    p=((1.055 * pow(b,vec3(1./2.4)))-.055);
    b*=12.92;x2r(r);x2r(g);x2r(b);
    return sat(b);}
    #define l2x(y) a.y=mix(p.y,(a.y+b1)/7.787,step(p.y,.008856))
vec3 lab2xyz (in vec3 l){vec3 a,p;const float b1=-16./116.;
    a.y=(l.x+16.)/116.;a.x=l.y/500.+ a.y;a.z=a.y-l.z/200.;//.xz include .y
    p=pow(a,vec3(3.));l2x(x);l2x(y);l2x(z);return a*vec3(95.047,100.,108.883);}
vec3 lab2rgb (in vec3 a){return xyz2rgb(lab2xyz(a));}


const vec3 lab0=vec3( 20.,100.,-50.);
const vec3 lab1=vec3(100., 50.,-50.);

void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
    vec2 A; // First gradient point.
    if(iMouse.z > 0.0) {
        A = vec2(iMouse);
    } else {
        A = vec2(
        (sin(iTime * 0.1) * 0.5 + .5) * iResolution.x,
        (cos(iTime * 0.3) * 0.5 + .5) * iResolution.y
        );
    }
    vec2 B = vec2(
    (sin(-iTime * -0.2) * 0.5 + .50) * iResolution.x,
    (-cos(iTime * -0.1) * 0.5 + .50) * iResolution.y
    ); // Second gradient point.

    vec2 V = B - A;

    float s = dot(fragCoord.xy-A, V) / dot(V, V); // Vector projection.
    s = clamp(s, 0.0, 1.0); // Saturate scaler.

    // color = pow(color, vec3(1.0/1.0)); // sRGB gamma encode.
    fragColor = vec4(lab2rgb(vec3(
    (sin(iTime + s * 20.0) + 1.0) / 2.0 * 50.0 + 25.0,
    cos(s + iTime) * 100.0,
    sin(s * 6.283) * 100.0
    )), 1.0);
}
