// Tuto: Jacobians and undistortion
// from https://www.shadertoy.com/view/WlByRW

#define Circle(U,r) smoothstep(50./R.y,0., abs(length(U)-r)-.02 )

void mainImage( out vec4 O, vec2 u )
{
    vec2 R = iResolution.xy,
    U = ( 2.*u - R ) / R.y, I, L;                  // normalized coordinates

    U += .5*U*mat2(cos(2.*U+iTime),sin(2.*U-iTime));   // distorted coordinates
    U *= 4.;

    I =floor(U); L = 2.*fract(U)-1.;                   // draw distorted checker
    O = vec4 (.4*mod(I.x+I.y,2.) );

    O.r += Circle(L,.4);                               // draw circle in distorted space

    mat2 J = transpose(mat2(dFdx(U),dFdy(U))) *R.y/8.; // the Jacobian store the local distorted frame
    L *= inverse(J);                                   // undistort by applying the inverse transform
    // or if you prefer right-multiplying matrices :
    // mat2 J = mat2(dFdx(U),dFdy(U)) *R.y/8.;            // the Jacobian store the local distorted frame
    // L = inverse(J)*L;                                  // undistort by applying the inverse transform
    O.g += Circle(L,.4);                               // draw circle in undistorted space

    O.b = ( .5+.5*determinant(J) ) *.6;                // det(J) shows compression in distortion
    //O.b = length(vec4(J))/3.;
    O.a = 1.;
}