// Sheep wobbly lines thing I'm really bad at naming these lmao
// Ben, BAAAHS 2023

// Built off of https://www.shadertoy.com/view/mdBBRW

// Copy-paste this to add a mock beatInfo object to shadertoy for testing purposes
#define time iTime
#define resolution iResolution
#define BPM 125.0
// These will need to be Find-Replaced from beatInfo_beat -> beatInfo.beat
#define beatInfo_beat mod(time * (BPM /60.), 4.)
#define beatInfo_bpm BPM
#define beatInfo_intensity (.5*smoothstep(1., 0., mod(beatInfo_beat, 1.) / 0.4) + .5*smoothstep(1., 0., (1. - mod(beatInfo_beat, 1.)) / 0.2))
#define beatInfo_confidence 1.0

const float PI = 3.141592653589;
const float LOGO_RADIUS = 0.5;
const float LOGO_BRIGHTNESS = 0.2;
const float FREQ_RANGE = 64.;
const float speed = 0.8;


vec3 palette (float t) {
    vec3 a = vec3(0.660, 0.560, 0.680);
    vec3 b = vec3(0.718, 0.438, 0.720);
    vec3 c = vec3(0.520, 0.100, 0.520);
    vec3 d = vec3(-0.60, -0.30, -0.09);
    return a + b*cos(6.28318*(c*(t+20.)+d));
}

/* Minified BAAAHS logo code: use pointInSheep(vec2 point, float scale, vec2 offset) to determine if a pixel is in the sheep logo. */ float COORD_SCALE = 0.03; vec2 HEART[187] = vec2[187](vec2(-1.408e1,2.96),vec2(-1.39e1,3.56),vec2(-1.326e1,3.86),vec2(-1.37e1,4.4),vec2(-1.36e1,5.08),vec2(-1.322e1,5.4),vec2(-1.268e1,5.46),vec2(-13,5.92),vec2(-1.292e1,6.52),vec2(-1.242e1,6.92),vec2(-1.174e1,6.84),vec2(-1.198e1,7.4),vec2(-1.174e1,7.98),vec2(-1.126e1,8.22),vec2(-1.054e1,8.02),vec2(-1.058e1,8.66),vec2(-1.012e1,9.14),vec2(-9.6,9.18),vec2(-9.1,8.88),vec2(-9.08,9.42),vec2(-8.62,9.86),vec2(-8,9.86),vec2(-7.54,9.42),vec2(-7.4,9.94),vec2(-6.88,1.028e1),vec2(-6.22,1.014e1),vec2(-5.9,9.56),vec2(-5.46,1.01e1),vec2(-4.78,1.014e1),vec2(-4.42,9.88),vec2(-4.22,9.32),vec2(-3.86,9.8),vec2(-3.2,9.88),vec2(-2.74,9.54),vec2(-2.6,8.88),vec2(-2.02,9.24),vec2(-1.38,9.04),vec2(-1.12,8.6),vec2(-1.14,8.06),vec2(-0.52,8.28),vec2(0,8.02),vec2(0.58,8.16),vec2(1.04,7.96),vec2(1,8.5),vec2(1.28,8.96),vec2(1.98,9.14),vec2(2.54,8.78),vec2(2.52,9.3),vec2(2.98,9.8),vec2(3.6,9.82),vec2(4.12,9.3),vec2(4.22,9.72),vec2(4.62,1.008e1),vec2(5.32,1.006e1),vec2(5.78,9.52),vec2(6.1,1.014e1),vec2(6.74,1.028e1),vec2(7.24,9.98),vec2(7.4,9.44),vec2(7.98,9.92),vec2(8.64,9.82),vec2(8.98,9.4),vec2(8.98,8.88),vec2(9.5,9.24),vec2(1.02e1,9.1),vec2(1.052e1,8.58),vec2(1.046e1,8.04),vec2(1.11e1,8.3),vec2(1.166e1,8.04),vec2(1.188e1,7.38),vec2(1.166e1,6.92),vec2(1.236e1,7),vec2(1.284e1,6.62),vec2(1.294e1,6.06),vec2(1.268e1,5.54),vec2(1.326e1,5.48),vec2(1.368e1,4.92),vec2(1.364e1,4.44),vec2(1.326e1,3.98),vec2(1.378e1,3.82),vec2(1.41e1,3.24),vec2(1.394e1,2.66),vec2(1.34e1,2.36),vec2(1.386e1,2.08),vec2(1.408e1,1.62),vec2(1.388e1,0.94),vec2(1.32e1,0.68),vec2(1.36e1,0.16),vec2(1.356e1,-0.42),vec2(1.322e1,-0.8),vec2(1.262e1,-0.92),vec2(1.302e1,-1.38),vec2(1.294e1,-2.06),vec2(1.244e1,-2.44),vec2(1.188e1,-2.38),vec2(1.212e1,-2.92),vec2(1.198e1,-3.46),vec2(1.152e1,-3.8),vec2(1.094e1,-3.74),vec2(1.114e1,-4.44),vec2(1.08e1,-5.02),vec2(1.036e1,-5.18),vec2(9.86,-5.06),vec2(1.006e1,-5.66),vec2(9.84,-6.16),vec2(9.3,-6.42),vec2(8.72,-6.22),vec2(8.82,-6.92),vec2(8.46,-7.42),vec2(7.88,-7.54),vec2(7.48,-7.38),vec2(7.62,-8),vec2(7.34,-8.48),vec2(6.74,-8.68),vec2(6.26,-8.46),vec2(6.36,-9.14),vec2(5.88,-9.7),vec2(5.34,-9.74),vec2(4.94,-9.48),vec2(4.98,-1.016e1),vec2(4.64,-1.058e1),vec2(4.18,-1.072e1),vec2(3.56,-1.046e1),vec2(3.64,-1.102e1),vec2(3.3,-1.152e1),vec2(2.76,-1.166e1),vec2(2.22,-1.14e1),vec2(2.28,-1.198e1),vec2(1.92,-1.248e1),vec2(1.34,-1.26e1),vec2(0.86,-1.232e1),vec2(0.9,-13),vec2(0.74,-1.328e1),vec2(0.28,-1.354e1),vec2(-0.24,-1.35e1),vec2(-0.6,-1.324e1),vec2(-0.76,-1.242e1),vec2(-1.22,-1.266e1),vec2(-1.84,-1.25e1),vec2(-2.14,-1.204e1),vec2(-2.08,-1.146e1),vec2(-2.62,-1.172e1),vec2(-3.24,-1.152e1),vec2(-3.52,-11),vec2(-3.42,-1.052e1),vec2(-3.9,-1.076e1),vec2(-4.54,-1.062e1),vec2(-4.88,-1.006e1),vec2(-4.8,-9.54),vec2(-5.32,-9.82),vec2(-5.94,-9.64),vec2(-6.24,-9.1),vec2(-6.1,-8.5),vec2(-6.54,-8.74),vec2(-7.18,-8.58),vec2(-7.5,-8.02),vec2(-7.38,-7.44),vec2(-7.82,-7.62),vec2(-8.36,-7.48),vec2(-8.72,-6.88),vec2(-8.56,-6.32),vec2(-9.08,-6.5),vec2(-9.56,-6.36),vec2(-9.94,-5.72),vec2(-9.74,-5.14),vec2(-1.034e1,-5.26),vec2(-1.086e1,-4.96),vec2(-1.104e1,-4.34),vec2(-1.082e1,-3.82),vec2(-1.128e1,-3.92),vec2(-1.168e1,-3.76),vec2(-1.202e1,-3.1),vec2(-1.18e1,-2.48),vec2(-1.242e1,-2.54),vec2(-1.294e1,-2.02),vec2(-1.294e1,-1.46),vec2(-1.256e1,-1.04),vec2(-1.314e1,-0.92),vec2(-1.352e1,-0.46),vec2(-1.352e1,8.0e-2),vec2(-1.31e1,0.54),vec2(-1.382e1,0.78),vec2(-1.406e1,1.5),vec2(-1.382e1,1.98),vec2(-1.336e1,2.22),vec2(-1.386e1,2.46),vec2(-1.406e1,2.94)); vec2 HEART_MIN_XY = vec2(-70.5, -67.9); vec2 HEART_MAX_XY = vec2(70.6, 51.6); vec2 FACE[26] = vec2[26](vec2(-5.24,2.5),vec2(-5.18,3.56),vec2(-5.04,3.84),vec2(-4.8,3.88),vec2(-3.58,3.3),vec2(-1.88,2.12),vec2(2.16,2.12),vec2(4.02,3.68),vec2(4.78,4.04),vec2(5.18,4.02),vec2(5.38,3.64),vec2(5.44,2.72),vec2(5.12,0),vec2(2.84,-7.52),vec2(2.7,-8.46),vec2(2.36,-9.22),vec2(1.86,-9.8),vec2(1.14,-1.026e1),vec2(0.46,-1.046e1),vec2(-0.36,-1.044e1),vec2(-1.28,-1.008e1),vec2(-1.88,-9.58),vec2(-2.38,-8.8),vec2(-2.64,-7.5),vec2(-4.76,-0.72),vec2(-5.22,2.48)); vec2 MOUTH[35] = vec2[35](vec2(-1.66,-6.76),vec2(-1.56,-6.52),vec2(-1.3,-6.58),vec2(-0.32,-7.72),vec2(0.2,-7.86),vec2(0.58,-7.68),vec2(1.4,-6.66),vec2(1.62,-6.52),vec2(1.84,-6.58),vec2(1.82,-6.84),vec2(1.52,-6.98),vec2(1.22,-7.28),vec2(0.3,-8.66),vec2(0.3,-9.18),vec2(0.4,-9.28),vec2(1.14,-9.04),vec2(1.66,-8.64),vec2(1.8,-8.68),vec2(1.78,-8.82),vec2(1.44,-9.2),vec2(0.98,-9.52),vec2(0.42,-9.78),vec2(2.0e-2,-9.84),vec2(-0.86,-9.46),vec2(-1.54,-8.82),vec2(-1.56,-8.68),vec2(-1.44,-8.64),vec2(-1,-8.98),vec2(-0.18,-9.28),vec2(-8.0e-2,-9.16),vec2(-8.0e-2,-8.74),vec2(-0.14,-8.5),vec2(-0.8,-7.54),vec2(-1.26,-7.02),vec2(-1.64,-6.76)); vec2 EYE_LEFT[10] = vec2[10](vec2(-4.6,2.4),vec2(-4.6,2.72),vec2(-4.46,2.98),vec2(-4.14,3.02),vec2(-3.82,2.8),vec2(-3.48,2.14),vec2(-3.54,1.6),vec2(-4,1.6),vec2(-4.26,1.72),vec2(-4.58,2.38)); vec2 EYE_RIGHT[10] = vec2[10](vec2(3.64,1.8),vec2(3.72,2.34),vec2(4,2.8),vec2(4.32,3.02),vec2(4.68,2.94),vec2(4.78,2.42),vec2(4.48,1.76),vec2(4.2,1.6),vec2(3.7,1.6),vec2(3.66,1.78)); vec2 EAR_LEFT[9] = vec2[9](vec2(-1.092e1,6.08),vec2(-1.084e1,6.26),vec2(-1.058e1,6.3),vec2(-7.02,6.24),vec2(-6.94,4.48),vec2(-7.14,4.38),vec2(-7.84,4.42),vec2(-1.078e1,5.88),vec2(-1.09e1,6.06)); vec2 EAR_RIGHT[10] = vec2[10](vec2(7.22,4.48),vec2(7.42,4.38),vec2(8.12,4.42),vec2(1.106e1,5.88),vec2(1.12e1,6.14),vec2(1.112e1,6.26),vec2(1.086e1,6.3),vec2(7.36,6.28),vec2(7.26,6.18),vec2(7.22,4.5));  bool pointInHeart(vec2 point, float scale, vec2 offset){ 	int len = 187; 	int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE); 	for (i = 0, j = len-1; i < len; j = i++) { if ( ((HEART[i].y > p.y) != (HEART[j].y > p.y)) && (p.x < (HEART[j].x-HEART[i].x) * (p.y-HEART[i].y) / (HEART[j].y-HEART[i].y) + HEART[i].x) ) c = !c; } 	return c; } bool pointInFace(vec2 point, float scale, vec2 offset){ 	int len = 26; 	int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE); 	for (i = 0, j = len-1; i < len; j = i++) { if ( ((FACE[i].y > p.y) != (FACE[j].y > p.y)) && (p.x < (FACE[j].x-FACE[i].x) * (p.y-FACE[i].y) / (FACE[j].y-FACE[i].y) + FACE[i].x) ) c = !c; } 	return c; } bool pointInMouth(vec2 point, float scale, vec2 offset){ 	int len = 35; 	int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE); 	for (i = 0, j = len-1; i < len; j = i++) { if ( ((MOUTH[i].y > p.y) != (MOUTH[j].y > p.y)) && (p.x < (MOUTH[j].x-MOUTH[i].x) * (p.y-MOUTH[i].y) / (MOUTH[j].y-MOUTH[i].y) + MOUTH[i].x) ) c = !c; } 	return c; } bool pointInEyeLeft(vec2 point, float scale, vec2 offset){ 	int len = 10; 	int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE); 	for (i = 0, j = len-1; i < len; j = i++) { if ( ((EYE_LEFT[i].y > p.y) != (EYE_LEFT[j].y > p.y)) && (p.x < (EYE_LEFT[j].x-EYE_LEFT[i].x) * (p.y-EYE_LEFT[i].y) / (EYE_LEFT[j].y-EYE_LEFT[i].y) + EYE_LEFT[i].x) ) c = !c; } 	return c; } bool pointInEyeRight(vec2 point, float scale, vec2 offset){ 	int len = 10; 	int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE); 	for (i = 0, j = len-1; i < len; j = i++) { if ( ((EYE_RIGHT[i].y > p.y) != (EYE_RIGHT[j].y > p.y)) && (p.x < (EYE_RIGHT[j].x-EYE_RIGHT[i].x) * (p.y-EYE_RIGHT[i].y) / (EYE_RIGHT[j].y-EYE_RIGHT[i].y) + EYE_RIGHT[i].x) ) c = !c; } 	return c; } bool pointInEarLeft(vec2 point, float scale, vec2 offset){ 	int len = 9; 	int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE); 	for (i = 0, j = len-1; i < len; j = i++) { if ( ((EAR_LEFT[i].y > p.y) != (EAR_LEFT[j].y > p.y)) && (p.x < (EAR_LEFT[j].x-EAR_LEFT[i].x) * (p.y-EAR_LEFT[i].y) / (EAR_LEFT[j].y-EAR_LEFT[i].y) + EAR_LEFT[i].x) ) c = !c; } 	return c; } bool pointInEarRight(vec2 point, float scale, vec2 offset){ 	int len = 10; 	int i, j; bool c = false; vec2 p = (point - offset)/(scale * COORD_SCALE); 	for (i = 0, j = len-1; i < len; j = i++) { if ( ((EAR_RIGHT[i].y > p.y) != (EAR_RIGHT[j].y > p.y)) && (p.x < (EAR_RIGHT[j].x-EAR_RIGHT[i].x) * (p.y-EAR_RIGHT[i].y) / (EAR_RIGHT[j].y-EAR_RIGHT[i].y) + EAR_RIGHT[i].x) ) c = !c; } 	return c; }  bool pointInSheep(vec2 point, float scale, vec2 offset) { 	vec2 p = (point - offset)/(scale * COORD_SCALE); 	if (p.x > HEART_MAX_XY.x || p.y > HEART_MAX_XY.y || p.x < HEART_MIN_XY.x || p.y < HEART_MIN_XY.y) { 		return false; 	} 	bool inHeart = pointInHeart(point, scale, offset); 	bool inFace = pointInFace(point, scale, offset); 	bool inMouth = pointInMouth(point, scale, offset); 	bool inEyeLeft = pointInEyeLeft(point, scale, offset); 	bool inEyeRight = pointInEyeRight(point, scale, offset); 	bool inEarLeft = pointInEarLeft(point, scale, offset); 	bool inEarRight = pointInEarRight(point, scale, offset); 	return inHeart && (!(inFace || inEarLeft || inEarRight) || (inMouth || inEyeLeft || inEyeRight)); }

float getfrequency(float x) { return .1 + .2 * beatInfo_intensity;  /* texture(soundAnalysis.buckets, vec2(floor(x * FREQ_RANGE + 1.0) / FREQ_RANGE, 0.25)).x + 0.06; */ }
float getfrequency_smooth(float x) { float index = floor(x * FREQ_RANGE) / FREQ_RANGE; float next = floor(x * FREQ_RANGE + 1.0) / FREQ_RANGE; return mix(getfrequency(index), getfrequency(next), smoothstep(0.0, 1.0, fract(x * FREQ_RANGE))); }
float getfrequency_blend(float x) { return mix(getfrequency(x), getfrequency_smooth(x), 0.5); }

float brightnessFunction(vec2 fragPos) {
    float angle = atan(fragPos.x, fragPos.y);
    return max(getfrequency_blend(abs(angle / PI)) - 0.02, 0.0);
}

vec3 colorFunction_lensFlare(vec2 fragPos) {
    bool VERTICAL_LENS_FLARE = false;
    float lensFlareThickness = 10.;
    float glowFactor = 0.06;
    float perpendicularFalloff = 0.8 * LOGO_RADIUS;

    vec3 baseColor = .2 + 1.3*palette(length(fragPos / iResolution.y));
    //vec3 baseColor = vec3(1., 1., 1.);
    vec3 highlightColor = .1 * baseColor; //vec3(1., 1., 1.);

    float parallel, perp;
    if (VERTICAL_LENS_FLARE) {
        parallel = fragPos.y / lensFlareThickness;
        perp = max(abs(fragPos.x / lensFlareThickness)-0.001, 0.00125);
    } else {
        parallel = fragPos.x / lensFlareThickness;
        perp = max(abs(fragPos.y / lensFlareThickness)-0.001, 0.00125);
    }
    float perpendicularBrightness = smoothstep(0.667*perpendicularFalloff, 0.2*perpendicularFalloff, perp) * perpendicularFalloff / perp;
    return glowFactor * perpendicularBrightness * ((1.+parallel)*baseColor * cos(parallel+highlightColor));
}

vec3 drawBaaahsLogo(vec2 fragCoord, float scale) {
    vec2 fragPos = fragCoord / resolution.xy;
    fragPos = (fragPos - 0.5) * 2.0;
    fragPos.x *= resolution.x / resolution.y;

    float brightness = length(fragPos) < scale ? LOGO_BRIGHTNESS * 0.2 : LOGO_BRIGHTNESS;

    float sheepScaleRelativeToRing = 1.9 * scale;
    vec2 sheepXY = vec2(0., 0.);

    if (pointInSheep(fragPos, sheepScaleRelativeToRing, sheepXY)) { brightness *= 50.; }

    vec3 col = vec3(0.0);

    float ring = 1.0 / abs(length(fragPos) - scale);
    col += colorFunction_lensFlare(fragPos) * ring * brightness;

    col *= brightnessFunction(fragPos);

    return col;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 fragPos = fragCoord / iResolution.xy;
    fragPos = (fragPos - 0.5) * 2.0;
    fragPos.x *= iResolution.x / iResolution.y;

    vec2 uv = (fragCoord * 2.0 - iResolution.xy) / iResolution.y;


    float logoRadius = LOGO_RADIUS * (1. + 0.2 * (beatInfo_intensity));
    float radiusFalloff = 5.;
    float dR = max(length(fragPos) - logoRadius - 0.2, 0.);

    vec2 uv0 = uv;

    uv0 /= (1. + 4.*exp(-radiusFalloff*dR) * logoRadius / LOGO_RADIUS);
    vec3 finalColor = vec3(0.0);

    for (float i = 0.0; i < 4.0; i++) {
        uv = uv - .5 + i;

        uv *= sin(i*5. - length(uv0));

        vec3 col = palette(length(uv0) - time*speed);

        float factor = 1.;
        float d = factor * (length(uv) * exp(-length(factor*uv0)));
        d = sin(d*9.+ time*speed) * 0.5;
        d = abs(d);

        float thickness = 0.05;
        float thicknessPower = 1.5;
        d = pow(thickness / d, thicknessPower);



        finalColor += col * d;
    }

    finalColor *= 1.2/length(uv0);

    //finalColor *= (.9 + .2 * beatInfo_intensity * exp(-radiusFalloff*dR));

    if (length(fragPos) < logoRadius) {
        finalColor *= 0.;
    }

    finalColor += drawBaaahsLogo(fragCoord, logoRadius);

    fragColor = vec4(finalColor, 1.0);
}