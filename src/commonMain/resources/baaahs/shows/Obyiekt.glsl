// + Неопознанный Вращающийся Летающий Объект . пятница 13 дек 2019
#ifdef GL_ES
precision mediump float;
#endif

//#extension GL_OES_standard_derivatives : enable

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

#define PI 3.14159265359

mat2 rotate2d(float  ugol_povorota){ // обычная матрица поворота по углу
    return mat2(cos(ugol_povorota),-sin(ugol_povorota), sin(ugol_povorota),cos(ugol_povorota));
}

void main( void ) { // функция вызывается для каждой точки(gl_FragCoord.xy) на экране

    float N = 5.; 					// число граней 24
    //N = floor(fract(time*0.2)*7.0)+3.0; 		// Меняет число граней с 3х до 9 по факториалу от времени
    vec2 st = 2.*(gl_FragCoord.xy-.5*resolution.xy)/resolution.y;
    //st.x *= resolution.x/resolution.y ; 		// пропорция
    //st += st *2.-1.; 				// смещение центра координат
    st = rotate2d( sin(time*.2)*PI ) * st;   	// Вращение
    //st += vec2(cos(time),sin(time))*0.45;		// Смещение
    vec3 col = 0.5*(1. + cos(time +st.xyx+vec3(0,2,4)));
    float a = atan(st.x,st.y)+PI;
    float r = PI*2.0/N;
    float dd = 0.0;
    dd = cos(floor(.5+a/r)*r-a)*length(st);
    vec3 coltrian = vec3(1.0-smoothstep(.2,.21,dd))+vec3(dd);
    gl_FragColor = vec4(1.-coltrian + col ,1.0);

}