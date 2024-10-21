// Wobbly Color Grid
// From http://glslsandbox.com/e#60808.1

//  modded by Mik - v1.1

#ifdef GL_ES
precision mediump float;
#endif

//#extension GL_OES_standard_derivatives : enable

//varying vec2 surfacePosition;
uniform float time; // @@Time
uniform vec2 center; // @@XyPad
uniform vec2 resolution; // @@Resolution

void main(void){

    float PI = 3.141592;
    vec2 p = gl_FragCoord.xy/resolution*2.;

    float time = time + length(p)*cos(time/4. - .0001*length(p)*cos(time/PI + .0001*length(p)*cos(time*time/PI)));
    vec3 color = vec3(0., length(p), 0.);

    float f = 0.0;

    for(float i = 0.0; i < 15.0; i++){

        float s = sin(center.x +time + center.x * i * PI / 10.0) * .85;
        float c = cos(center.y + time + center.y * i * PI / 10.0) * .93;

        f += 0.001 / (abs(p.x + c / (.35+length(p))) * abs(p.y + s / (.35+length(p)))) / (.5+length(p));
        color.r += s * 1.0 * PI;
        color.b += c * 1.0 * PI;
    }


    gl_FragColor = vec4(vec3(f * color), 1.0);
}