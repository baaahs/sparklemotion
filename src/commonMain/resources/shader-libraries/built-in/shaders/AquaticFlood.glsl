// Aquatic Flood
// From http://glslsandbox.com/e#46102

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;
// SPARKLEMOTION GADGET: Beat { "name": "beat" }
uniform float sm_beat;

void main(void)
{
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    //uv.x *= resolution.x/resolution.y;
    float dist = 0.;
    uv.x = -2.+4.*uv.x;
    uv.y = -1.+2.*uv.y;
    // comment the next line to see the fully zoomed out view
    uv *=pow(.1,4.+cos(.1*time));
    uv.x += .275015;//;
    uv.y += .0060445;//
    //uv /= 5.;
    //vec4 col =vec4(1.);
    vec2 z = vec2(0.0);

    float warp_factor = 1.;
    float time_warp = sm_beat / (1. / warp_factor) - (warp_factor / 2.) + 1.;
    vec4 beat_flash = vec4(time_warp);

    int trap=0;
    for(int i = 0; i < 400; i++){
        if(dot(z,z)>4.){trap = i;break;}
        dist = min( 1e20, dot(z,z))+cos(float(i)*12.+3.*time);
        z = mat2(z,-z.y,z.x)*z + uv;
    }
    dist = sqrt(dist);
	float orb = sqrt(float(trap))/64.;
    gl_FragColor=vec4(0.,log(dist)*sqrt(dist)-orb-orb,log(dist)*sqrt(dist-abs(sin(time))),1.)*beat_flash;
    //if(orb == 0.){gl_FragColor = vec4(0.);}
    //gl_FragColor = (orb!=0. ? 1.-orb*vec4(9.,5.,3.,0.):vec4(0.));
}
