// Red Bristles
// From http://glslsandbox.com/e#60843.0

/*
 * Original shader from: https://www.shadertoy.com/view/4ljGWt
 */

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// shadertoy emulation
#define iTime time
#define iResolution resolution

// --------[ Original ShaderToy begins here ]---------- //
void mainImage(out vec4 c, vec2 q)
{
    float a=iTime*.1+1.,b=.5,g,e,t=0.,s;
    vec3 r=vec3(0.,0.,3.),w=normalize(vec3((q-iResolution.xy/2.)/iResolution.y,-.5)),p;

    mat2 x=mat2(cos(a),sin(a),sin(a),-cos(a)),y=mat2(cos(b),sin(b),sin(b),-cos(b));

    w.xz=y*w.xz;
    r.xz=y*r.xz;

    w.yz=x*w.yz;
    r.yz=x*r.yz;

    c.rgb=vec3(0.,0.,.02);

    for(int i=0;i<150;++i)
    {
        p=r+w*t;

        float f=.25,d=1e4;
        for(int j=0;j<2;++j)
        {
            s=.2*dot(p,p);
            p=p/s;
            f*=s;
            g=p.z;
            e=atan(p.y,p.x);
            p=(mod(p,2.)-1.)*1.25;
        }

        d=min((length(abs(p.xy)-1.3)-.1)*f,1e2);

        if(d<1e-3)
        break;

        c.rgb+=vec3(.9,.2,.1)*(pow(.5+.5*cos(g*.5+a*77.+cos(e*10.)),16.))*
        (1.-smoothstep(0.,1.,70.*d))*.25;

        t+=d;
    }
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
    gl_FragColor.a = 1.0;
}