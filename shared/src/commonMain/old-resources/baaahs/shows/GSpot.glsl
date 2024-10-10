// G-spot
// From: https://www.shadertoy.com/view/ltBcRc


#define l 120
void mainImage(out vec4 FragColor,vec2 FragCoord){
    vec2 v = (FragCoord.xy - iResolution.xy/2.) / min(iResolution.y,iResolution.x) * 30.;
    vec2 vv = v;// vec2 vvv = v;
    float ft = iTime+360.1;
    float tm = ft*0.1;
    float tm2 = ft*0.3;
    vec2 mspt = (vec2(
    sin(tm)+cos(tm*0.2)+sin(tm*0.5)+cos(tm*-0.4)+sin(tm*1.3),
    cos(tm)+sin(tm*0.1)+cos(tm*0.8)+sin(tm*-1.1)+cos(tm*1.5)
    )+1.0)*0.35; //5x harmonics, scale back to [0,1]
    float R = 0.0;
    float RR = 0.0;
    float RRR = 0.0;
    float a = (1.-mspt.x)*0.5;
    float C = cos(tm2*0.03+a*0.01)*1.1;
    float S = sin(tm2*0.033+a*0.23)*1.1;
    float C2 = cos(tm2*0.024+a*0.23)*3.1;
    float S2 = sin(tm2*0.03+a*0.01)*3.3;
    vec2 xa=vec2(C, -S);
    vec2 ya=vec2(S, C);
    vec2 xa2=vec2(C2, -S2);
    vec2 ya2=vec2(S2, C2);
    vec2 shift = vec2( 0.033, 0.14);
    vec2 shift2 = vec2( -0.023, -0.22);
    float Z = 0.4 + mspt.y*0.3;
    float m = 0.99+sin(iTime*0.03)*0.003;
    for ( int i = 0; i < l; i++ ){
        float r = dot(v,v);
        float r2 = dot(vv,vv);
        if ( r > 1.0 )
        {
            r = (1.0)/r ;
            v.x = v.x * r;
            v.y = v.y * r;
        }
        if ( r2 > 1.0 )
        {
            r2 = (1.0)/r2 ;
            vv.x = vv.x * r2;
            vv.y = vv.y * r2;
        }
        R *= m;
        R += r;
        R *= m;
        R += r2;
        if(i < l-1){
            RR *= m;
            RR += r;
            RR *= m;
            RR += r2;
            if(i < l-2){
                RRR *= m;
                RRR += r;
                RRR *= m;
                RRR += r2;
            }
        }

        v = vec2( dot(v, xa), dot(v, ya)) * Z + shift;
        vv = vec2( dot(vv, xa2), dot(vv, ya2)) * Z + shift2;
    }

    float c = ((mod(R,2.0)>1.0)?1.0-fract(R):fract(R));
    float cc = ((mod(RR,2.0)>1.0)?1.0-fract(RR):fract(RR));
    float ccc = ((mod(RRR,2.0)>1.0)?1.0-fract(RRR):fract(RRR));
    FragColor = vec4(ccc, cc, c, 1.0);
}