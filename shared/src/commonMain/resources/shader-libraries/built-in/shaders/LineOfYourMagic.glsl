// https://www.shadertoy.com/view/M3V3Rc

/*originals
combination from many sources I’m just learning myself
At least someone somewhere has won,
just as we will win. Where success accompanies all of you,
success accompanies that they do not fight with us

MIT LICENSE
*/


#define STEPS 200.0
#define MDIST 150.0
#define pi 3.1415926535
#define pmod(p,x) (mod(p,x)-0.5*(x))
mat2 rot(float a){return mat2(cos(a),sin(a),-sin(a),cos(a));}

float box(vec3 p, vec3 b){
    vec3 d = abs(p)-b;
    d.xy*=rot(iTime);
    return max(d.x,max(d.y,d.z));
}

vec3 glow;

vec2 pmodp(vec2 p, float x){
    float a = atan(p.y,p.x);
    a = mod(a-x/2.,x)-x/2.;
    return vec2 (cos(a),sin(a))*length(p);
}
float vSeg(vec3 p, float h, float r){
    p.y -= clamp(p.y, 0.0, h);
    p.x *= clamp(p.x, 0.0, h);
    return length(p)-r;
}

vec2 map(vec3 p){
    vec2 a = vec2(1);
    vec2 b = vec2(22);
    vec3 po = p;
    float t= iTime;


    //Log-polar mapping from
    //https://www.osar.fr/notes/logspherical/
    vec2 p2 = p.xz;
    float r = length(p2);
    p2 = vec2(log(r),atan(p2.y,p2.x));
    float scl = 12.0/pi;

    t*=0.065;
    p2.y+=t;
    float yoff = 0.5;



    p2 *= scl;

    vec2 id = vec2(floor((p2.x)/1.5)+0.5,floor(p2.y/1.5)+0.5);
    ;
    p2 = pmod(p2,1.5);



    float dSpd = 10.0;
    float uSpd = 10.0;

    //apply up/down warp-in motion
    float ring =smoothstep(6.0,9.0,id.x-t*scl*(2.0/3.0))*yoff*dSpd*floor(mod(id.x,2.0));
    ring -= smoothstep(6.0,9.0,id.x-t*scl*(2.0/3.0))*yoff*uSpd*floor(mod(id.x+1.0,2.0));
    p.y -= ring;

    float mul = r/(scl);

    vec3 p3 = vec3(p2.x, p.y/mul,p2.y);

    //======BEGIN NON-DOMAIN WARPED SDF======

    //Boxes
    a.x = box(p3,vec3(0.6,0.6+0.15,0.6));
    a.x = max(a.x,-box(p3,vec3(3.0*cos(iTime),0.3,0.3)));
    //a.x-=0.1;

    //anti overstep artifact hidden stuff
    vec3 d = abs(p3)-1.0;
    float outerBox = max(d.x,d.z);
    if(length(po.xz)>0.1) a.x = min(-outerBox,a.x);

    //=======END NON-DOMAIN WARPED SDF=======
    a.x*=mul;

    //if(length(po.xz)<0.3) a.x = 0.1;
    a.x = max(a.x,(length(p.xz)-20.0));
    a.x = max(a.x,-(length(p.xz)-0.3)); //this ended up being useless

    p = po;
    //GLOWY BEAM THING
    b.x = length(p.xz)-0.8-sin(p.y-t*10.0)*0.2-sin(p.y*0.3-t*20.0)*0.2-abs(p.y)*0.2;
    glow+=vec3(0.529,0.400,1.000)*0.1/(0.1+b.x*b.x);

    a =(a.x<b.x)?a:b;


    float alpha = atan(p.z,p.x);
    p.y = abs(p.y)-9.0*cos(iTime);

    p-=vec3(17,sin(alpha*3.0-t)*cos(iTime)*2.0,24.*cos(iTime));

    p.y+=2.;


    b.y = 3.0*cos(iTime);
    a =(a.x<b.x)?a:b;

    return a;
}


vec3 norm(vec3 p, float s){
    vec2 e= vec2(s,0);
    return normalize(vec3(
    map(p+e.xyy).x,map(p+e.yxy).x,
    map(p+e.yyx).x)-vec3(map(p-e.xyy).x,
    map(p-e.yxy).x,map(p-e.yyx).x));
}


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = (fragCoord-0.5*iResolution.xy)/iResolution.y;
    vec3 col = vec3(0);

    vec3 ro = vec3(10.,10.,-20.0);
    ro.xz*=rot(iTime);
    vec3 lk = vec3(0,1,0);
    vec3 f = normalize(lk-ro);
    vec3 r = normalize(cross(vec3(1,1,0),f));
    vec3 rd = normalize(f*0.7+uv.x*r+uv.y*cross(f,r));
    r.yz*=rot(iTime);
    float dO, shad;
    bool hit = false;
    vec2 d; vec3 p;
    d.yx*=rot(iTime);
    for(float i = 0.0; i<STEPS; i++){
        p = ro+rd*dO;
        d = map(p);
        dO+= d.x*0.85;

        if(abs(d.x)<0.005){
            shad = i/STEPS;
            hit = true;
            break;
        }
        if(dO>MDIST){
            dO = MDIST; break;
        }
    }
    if(hit){
        vec3 n = norm(p,0.02);
        float edge = length(n-norm(p,0.12))*min(1.0,length(p)*0.2);
        //edge =smoothstep(0.,0.15,edge);

        if(d.y == 1.0) col = vec3(0.388);
        if(d.y == 2.0) col = vec3(0.502,0.000,1.000);
        if(d.y == 3.0) col = vec3(0.388);

        //Shamelessly stealing blackle's lighting techniques
        float ao = smoothstep(-.1,.1,map(p+n*.1).x)*
        smoothstep(-.3,.3,map(p+n*.3).x)*
        smoothstep(-.7,.7,map(p+n*.7).x);
        vec3 r = reflect(rd, n);
        float diff = length(sin(n*3.)*.7+.3)/sqrt(3.);
        float spec = length(sin(r*3.)*.5+.5)/sqrt(3.);
        float fres = 1.-abs(dot(rd,n))*.95;
        col = col*pow(diff,3.0) + pow(spec, 12.)*fres*vec3(0.463,0.490,0.898);
        col*=ao;

        if(d.y == 1.0)col+=smoothstep(0.,0.1,edge)*vec3(0.188,0.804,0.910);
        if(d.y == 3.0)col+=smoothstep(0.,0.15,edge)*vec3(0.659,0.188,0.910)*10.0;
    }

    col = mix(col,mix(vec3(0.075),vec3(0.),length(uv)),dO/MDIST);
    col+=glow*0.6;
    fragColor = vec4(col,1.0);
}
