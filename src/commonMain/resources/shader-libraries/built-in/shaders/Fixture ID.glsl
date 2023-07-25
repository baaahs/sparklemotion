// Fixture ID
// From https://www.shadertoy.com/view/XdXGRB, modified by Xian

// Source edited by David Hoskins - 2013.

// I took and completed this http://glsl.heroku.com/e#9743.20 - just for fun! 8|
// Locations in 3x7 font grid, inspired by http://www.claudiocc.com/the-1k-notebook-part-i/
// Had to edit it to remove some duplicate lines.
// ABC  a:GIOMJL b:AMOIG c:IGMO d:COMGI e:OMGILJ f:CBN g:OMGIUS h:AMGIO i:EEHN j:GHTS k:AMIKO l:BN m:MGHNHIO n:MGIO
// DEF  o:GIOMG p:SGIOM q:UIGMO r:MGI s:IGJLOM t:BNO u:GMOI v:GJNLI w:GMNHNOI x:GOKMI y:GMOIUS z:GIMO
// GHI
// JKL
// MNO
// PQR
// STU

vec2 coord;

#define font_size 20.
#define font_spacing .05
vec2 caret_origin = vec2(2.4, .7);
vec2 caret;

#define STROKEWIDTH 0.05
#define PI 3.14159265359

#define A_ vec2(0.,0.)
#define B_ vec2(1.,0.)
#define C_ vec2(2.,0.)

#define D_ vec2(0.,1.)
#define E_ vec2(1.,1.)
#define F_ vec2(2.,1.)

#define G_ vec2(0.,2.)
#define H_ vec2(1.,2.)
#define I_ vec2(2.,2.)

#define J_ vec2(0.,3.)
#define K_ vec2(1.,3.)
#define L_ vec2(2.,3.)

#define M_ vec2(0.,4.)
#define N_ vec2(1.,4.)
#define O_ vec2(2.,4.)

#define P_ vec2(0.,5.)
#define Q_ vec2(1.,5.)
#define R_ vec2(1.,5.)

#define S_ vec2(0.,6.)
#define T_ vec2(1.,6.)
#define U_ vec2(2.0,6.)

//  + t(_,_,p)
#define A(p) t(G_,I_,p) + t(I_,O_,p) + t(O_,M_, p) + t(M_,J_,p) + t(J_,L_,p);caret.x += 1.0;
#define B(p) t(A_,M_,p) + t(M_,O_,p) + t(O_,I_, p) + t(I_,G_,p);caret.x += 1.0;
#define C(p) t(I_,G_,p) + t(G_,M_,p) + t(M_,O_,p);caret.x += 1.0;
#define D(p) t(C_,O_,p) + t(O_,M_,p) + t(M_,G_,p) + t(G_,I_,p);caret.x += 1.0;
#define E(p) t(O_,M_,p) + t(M_,G_,p) + t(G_,I_,p) + t(I_,L_,p) + t(L_,J_,p);caret.x += 1.0;
#define F(p) t(C_,B_,p) + t(B_,N_,p) + t(G_,I_,p);caret.x += 1.0;
#define G(p) t(O_,M_,p) + t(M_,G_,p) + t(G_,I_,p) + t(I_,U_,p) + t(U_,S_,p);caret.x += 1.0;
#define H(p) t(A_,M_,p) + t(G_,I_,p) + t(I_,O_,p);caret.x += 1.0;
#define I(p) t(E_,E_,p) + t(H_,N_,p);caret.x += 1.0;
#define J(p) t(E_,E_,p) + t(H_,T_,p) + t(T_,S_,p);caret.x += 1.0;
#define K(p) t(A_,M_,p) + t(M_,I_,p) + t(K_,O_,p);caret.x += 1.0;
#define L(p) t(B_,N_,p);caret.x += 1.0;
#define M(p) t(M_,G_,p) + t(G_,I_,p) + t(H_,N_,p) + t(I_,O_,p);caret.x += 1.0;
#define N(p) t(M_,G_,p) + t(G_,I_,p) + t(I_,O_,p);caret.x += 1.0;
#define O(p) t(G_,I_,p) + t(I_,O_,p) + t(O_,M_, p) + t(M_,G_,p);caret.x += 1.0;
#define P(p) t(S_,G_,p) + t(G_,I_,p) + t(I_,O_,p) + t(O_,M_, p);caret.x += 1.0;
#define Q(p) t(U_,I_,p) + t(I_,G_,p) + t(G_,M_,p) + t(M_,O_, p);caret.x += 1.0;
#define R(p) t(M_,G_,p) + t(G_,I_,p);caret.x += 1.0;
#define S(p) t(I_,G_,p) + t(G_,J_,p) + t(J_,L_,p) + t(L_,O_,p) + t(O_,M_,p);caret.x += 1.0;
#define T(p) t(B_,N_,p) + t(N_,O_,p) + t(G_,I_,p);caret.x += 1.0;
#define U(p) t(G_,M_,p) + t(M_,O_,p) + t(O_,I_,p);caret.x += 1.0;
#define V(p) t(G_,J_,p) + t(J_,N_,p) + t(N_,L_,p) + t(L_,I_,p);caret.x += 1.0;
#define W(p) t(G_,M_,p) + t(M_,O_,p) + t(N_,H_,p) + t(O_,I_,p);caret.x += 1.0;
#define X(p) t(G_,O_,p) + t(I_,M_,p);caret.x += 1.0;
#define Y(p) t(G_,M_,p) + t(M_,O_,p) + t(I_,U_,p) + t(U_,S_,p);caret.x += 1.0;
#define Z(p) t(G_,I_,p) + t(I_,M_,p) + t(M_,O_,p);caret.x += 1.0;

#define n0(p) t(D_,B_,p) + t(B_,C_,p) + t(C_,L_, p) + t(L_,N_,p) + t(N_,M_, p) + t(M_,D_,p);caret.x += 1.0;
#define n1(p) t(D_,B_,p) + t(B_,N_,p);caret.x += 1.0;
#define n2(p) t(D_,B_,p) + t(B_,F_,p) + t(F_,J_,p) + t(J_,M_,p) + t(M_,O_,p);caret.x += 1.0;
#define n3(p) t(D_,B_,p) + t(B_,F_,p) + t(F_,H_,p) + t(H_,L_,p) + t(L_,N_,p);caret.x += 1.0;
#define n4(p) t(B_,D_,p) + t(D_,G_,p) + t(G_,I_,p) + t(F_,O_,p);caret.x += 1.0;
#define n5(p) t(C_,A_,p) + t(A_,D_,p) + t(D_,G_,p) + t(G_,H_,p) + t(H_,L_,p) + t(L_,N_,p) + t(N_,M_,p);caret.x += 1.0;
#define n6(p) t(B_,D_,p) + t(D_,J_,p) + t(J_,N_,p) + t(N_,L_,p) + t(L_,H_,p) + t(H_,J_,p);caret.x += 1.0;
#define n7(p) t(A_,C_,p) + t(C_,F_,p) + t(F_,M_,p);caret.x += 1.0;
#define n8(p) t(B_,F_,p) + t(F_,J_,p) + t(J_,N_,p) + t(N_,L_,p) + t(L_,D_,p) + t(D_,B_,p);caret.x += 1.0;
#define n9(p) t(I_,H_,p) + t(H_,D_,p) + t(D_,B_,p) + t(B_,F_,p) + t(F_,L_,p) + t(L_,N_,p) + t(N_,J_,p);caret.x += 1.0;

#define STOP(p) t(N_,N_,p);caret.x += 1.0;
#define HYPHEN(p) t(G_,I_,p);caret.x += 1.0;
#define UNKNOWN(p) t(D_,A_,p) + t(A_,C_,p) + t(C_,F_,p) + t(H_,H_,p) + t(J_,M_,p) + t(M_,O_,p) + t(O_,L_,p);caret.x += 1.0;

//-----------------------------------------------------------------------------------
float minimum_distance(vec2 v, vec2 w, vec2 p)
{	// Return minimum distance between line segment vw and point p
    float l2 = (v.x - w.x)*(v.x - w.x) + (v.y - w.y)*(v.y - w.y); //length_squared(v, w);  // i.e. |w-v|^2 -  avoid a sqrt
    if (l2 == 0.0) {
        return distance(p, v);   // v == w case
    }

    // Consider the line extending the segment, parameterized as v + t (w - v).
    // We find projection of point p onto the line.  It falls where t = [(p-v) . (w-v)] / |w-v|^2
    float t = dot(p - v, w - v) / l2;
    if(t < 0.0) {
        // Beyond the 'v' end of the segment
        return distance(p, v);
    } else if (t > 1.0) {
        return distance(p, w);  // Beyond the 'w' end of the segment
    }
    vec2 projection = v + t * (w - v);  // Projection falls on the segment
    return distance(p, projection);
}

//-----------------------------------------------------------------------------------
float textColor(vec2 from, vec2 to, vec2 p)
{
    p *= font_size;
    float inkNess = 0., nearLine, corner;
    nearLine = minimum_distance(from,to,p); // basic distance from segment, thanks http://glsl.heroku.com/e#6140.0
    inkNess += smoothstep(0., 1., 1.- 14.*(nearLine - STROKEWIDTH)); // ugly still
    inkNess += smoothstep(0., 2.5, 1.- (nearLine  + 5. * STROKEWIDTH)); // glow
    return inkNess;
}

//-----------------------------------------------------------------------------------
vec2 grid(vec2 letterspace)
{
    return ( vec2( (letterspace.x / 2.) * .65 , 1.0-((letterspace.y / 2.) * .95) ));
}

//-----------------------------------------------------------------------------------
float count = 0.0;
float gtime;
float t(vec2 from, vec2 to, vec2 p)
{
    count++;
    if (count > gtime*20.0) return 0.0;
    return textColor(grid(from), grid(to), p);
}

//-----------------------------------------------------------------------------------
vec2 r()
{
    vec2 pos = coord.xy;
    pos.y -= caret.y;
    pos.x -= font_spacing*caret.x;
    return pos;
}

//-----------------------------------------------------------------------------------
void _()
{
    caret.x += 1.5;
}

//-----------------------------------------------------------------------------------
void newline()
{
    caret.x = caret_origin.x;
    caret.y -= .18;
}

struct FixtureInfo {
    vec3 position;
    vec3 rotation;
    mat4 transformation;
    vec3 boundaryMin;
    vec3 boundaryMax;
    int name0;
    int name1;
    int name2;
    int name3;
    int name4;
    int name5;
    int name6;
    int name7;
};
uniform FixtureInfo fixtureInfo; // @@FixtureInfo

//-----------------------------------------------------------------------------------
void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    int text[8] = int[](
    fixtureInfo.name0, fixtureInfo.name1,
    fixtureInfo.name2, fixtureInfo.name3,
    fixtureInfo.name4, fixtureInfo.name5,
    fixtureInfo.name6, fixtureInfo.name7
    );

    coord = fragCoord / iResolution.xy - fixtureInfo.position.xy;
    float time = mod(iTime, 11.0);
    gtime = time;

    float d = 0.;
    vec3 col = vec3(0.1, .07+0.07*(.5+sin(coord.y*3.14159*1.1+time*2.0)) + sin(coord.y*.01+time+2.5)*0.05, 0.1);
    col = vec3(0.);

    caret = caret_origin;

    for (int c = 0; c < 8; c++) {
        int ch = text[c];
        if (ch == 0) { }
        else if (ch == 65) { d += A(r()) }
        else if (ch == 66) { d += B(r()) }
        else if (ch == 67) { d += C(r()) }
        else if (ch == 68) { d += D(r()) }
        else if (ch == 69) { d += E(r()) }
        else if (ch == 70) { d += F(r()) }
        else if (ch == 71) { d += G(r()) }
        else if (ch == 72) { d += H(r()) }
        else if (ch == 73) { d += I(r()) }
        else if (ch == 74) { d += J(r()) }
        else if (ch == 75) { d += K(r()) }
        else if (ch == 76) { d += L(r()) }
        else if (ch == 77) { d += M(r()) }
        else if (ch == 78) { d += N(r()) }
        else if (ch == 79) { d += O(r()) }
        else if (ch == 80) { d += P(r()) }
        else if (ch == 81) { d += Q(r()) }
        else if (ch == 82) { d += R(r()) }
        else if (ch == 83) { d += S(r()) }
        else if (ch == 84) { d += T(r()) }
        else if (ch == 85) { d += U(r()) }
        else if (ch == 86) { d += V(r()) }
        else if (ch == 87) { d += W(r()) }
        else if (ch == 88) { d += X(r()) }
        else if (ch == 89) { d += Y(r()) }
        else if (ch == 90) { d += Z(r()) }

        else if (ch == 32) { caret.x += 1.0; }
        else if (ch == 45) { d += HYPHEN(r()) }
        else if (ch == 46) { d += STOP(r()) }

        else if (ch == 48) { d += n0(r()) }
        else if (ch == 49) { d += n1(r()) }
        else if (ch == 50) { d += n2(r()) }
        else if (ch == 51) { d += n3(r()) }
        else if (ch == 52) { d += n4(r()) }
        else if (ch == 53) { d += n5(r()) }
        else if (ch == 54) { d += n6(r()) }
        else if (ch == 55) { d += n7(r()) }
        else if (ch == 56) { d += n8(r()) }
        else if (ch == 57) { d += n9(r()) }
        else { d += UNKNOWN(r()) }
    }

    // 	d = clamp(d* (.75+sin(fragCoord.x*PI*.5-time*4.3)*.5), 0.0, 1.0);

    col += vec3(d*.5, d, d*.85);
    vec2 xy = fragCoord.xy / iResolution.xy;
    // 	col *= vec3(.4, .4, .3) + 0.5*pow(100.0*xy.x*xy.y*(1.0-xy.x)*(1.0-xy.y), .4 );
    fragColor = vec4( col, 1.0 );
}
