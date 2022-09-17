// Some commonly used color functions
// Ben Bartlett

/* Convert HSV values ot RGB values
   Hue: cycles over range betwee 0.0 and 1.0; modulo 1
   Saturaton: 0-1
   Value: 0-1
   */
vec3 hsv2rgb(in vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

/* Convert HSL values ot RGB values
   Hue: cycles over range betwee 0.0 and 1.0; modulo 1
   Saturaton: 0-1
   Lightness: 0-1, pure colors at 0.5
   */
vec3 hsl2rgb(in vec3 c) {
    vec3 rgb = clamp( abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),6.0)-3.0)-1.0, 0.0, 1.0 );
    return c.z + c.y * (rgb-0.5)*(1.0-abs(2.0*c.z-1.0));
}
