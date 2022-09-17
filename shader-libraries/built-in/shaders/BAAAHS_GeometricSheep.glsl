// Make each physical panel a different color and change the colors on the beat
// Ben Bartlett

/* Minified color palettes library */
vec4 mix_color(vec4 color1, vec4 color2, float x) { return mix(color1, color2, vec4(x,x,x,x)); }
vec4 get_color(vec4 colors[11], float x) { int index = int(10.*x); if (index >= 10) { return colors[10]; } else { float remainder = (10. * x) - floor(10.*x); return mix_color(colors[index], colors[index+1], remainder); } }
vec4 viridis(float x) { return get_color(vec4[11](vec4(0.267004, 0.004874, 0.329415, 1.0), vec4(0.282623, 0.140926, 0.457517, 1.0), vec4(0.253935, 0.265254, 0.529983, 1.0), vec4(0.206756, 0.371758, 0.553117, 1.0), vec4(0.163625, 0.471133, 0.558148, 1.0), vec4(0.127568, 0.566949, 0.550556, 1.0), vec4(0.134692, 0.658636, 0.517649, 1.0), vec4(0.266941, 0.748751, 0.440573, 1.0), vec4(0.477504, 0.821444, 0.318195, 1.0), vec4(0.741388, 0.873449, 0.149561, 1.0), vec4(0.993248, 0.906157, 0.143936, 1.0)), x); }
vec4 plasma(float x) { return get_color(vec4[11](vec4(0.050383, 0.029803, 0.527975, 1.0), vec4(0.254627, 0.013882, 0.615419, 1.0), vec4(0.417642, 0.000564, 0.65839, 1.0), vec4(0.562738, 0.051545, 0.641509, 1.0), vec4(0.69284, 0.165141, 0.564522, 1.0), vec4(0.798216, 0.280197, 0.469538, 1.0), vec4(0.881443, 0.392529, 0.383229, 1.0), vec4(0.949217, 0.517763, 0.295662, 1.0), vec4(0.98826, 0.652325, 0.211364, 1.0), vec4(0.988648, 0.809579, 0.145357, 1.0), vec4(0.940015, 0.975158, 0.131326, 1.0)), x); }
vec4 inferno(float x) { return get_color(vec4[11](vec4(0.001462, 0.000466, 0.013866, 1.0), vec4(0.087411, 0.044556, 0.224813, 1.0), vec4(0.258234, 0.038571, 0.406485, 1.0), vec4(0.416331, 0.090203, 0.432943, 1.0), vec4(0.578304, 0.148039, 0.404411, 1.0), vec4(0.735683, 0.215906, 0.330245, 1.0), vec4(0.865006, 0.316822, 0.226055, 1.0), vec4(0.954506, 0.468744, 0.099874, 1.0), vec4(0.987622, 0.64532, 0.039886, 1.0), vec4(0.964394, 0.843848, 0.273391, 1.0), vec4(0.988362, 0.998364, 0.644924, 1.0)), x); }
vec4 magma(float x) { return get_color(vec4[11](vec4(0.001462, 0.000466, 0.013866, 1.0), vec4(0.078815, 0.054184, 0.211667, 1.0), vec4(0.232077, 0.059889, 0.437695, 1.0), vec4(0.390384, 0.100379, 0.501864, 1.0), vec4(0.550287, 0.161158, 0.505719, 1.0), vec4(0.716387, 0.214982, 0.47529, 1.0), vec4(0.868793, 0.287728, 0.409303, 1.0), vec4(0.967671, 0.439703, 0.35981, 1.0), vec4(0.994738, 0.62435, 0.427397, 1.0), vec4(0.99568, 0.812706, 0.572645, 1.0), vec4(0.987053, 0.991438, 0.749504, 1.0)), x); }
vec4 spring(float x) { return get_color(vec4[11](vec4(1.0, 0.0, 1.0, 1.0), vec4(1.0, 0.09803921568627451, 0.9019607843137255, 1.0), vec4(1.0, 0.2, 0.8, 1.0), vec4(1.0, 0.2980392156862745, 0.7019607843137254, 1.0), vec4(1.0, 0.4, 0.6, 1.0), vec4(1.0, 0.5019607843137255, 0.4980392156862745, 1.0), vec4(1.0, 0.6, 0.4, 1.0), vec4(1.0, 0.7019607843137254, 0.29803921568627456, 1.0), vec4(1.0, 0.8, 0.19999999999999996, 1.0), vec4(1.0, 0.9019607843137255, 0.0980392156862745, 1.0), vec4(1.0, 1.0, 0.0, 1.0)), x); }
vec4 summer(float x) { return get_color(vec4[11](vec4(0.0, 0.5, 0.4, 1.0), vec4(0.09803921568627451, 0.5490196078431373, 0.4, 1.0), vec4(0.2, 0.6, 0.4, 1.0), vec4(0.2980392156862745, 0.6490196078431373, 0.4, 1.0), vec4(0.4, 0.7, 0.4, 1.0), vec4(0.5019607843137255, 0.7509803921568627, 0.4, 1.0), vec4(0.6, 0.8, 0.4, 1.0), vec4(0.7019607843137254, 0.8509803921568627, 0.4, 1.0), vec4(0.8, 0.9, 0.4, 1.0), vec4(0.9019607843137255, 0.9509803921568627, 0.4, 1.0), vec4(1.0, 1.0, 0.4, 1.0)), x); }
vec4 autumn(float x) { return get_color(vec4[11](vec4(1.0, 0.0, 0.0, 1.0), vec4(1.0, 0.09803921568627451, 0.0, 1.0), vec4(1.0, 0.2, 0.0, 1.0), vec4(1.0, 0.2980392156862745, 0.0, 1.0), vec4(1.0, 0.4, 0.0, 1.0), vec4(1.0, 0.5019607843137255, 0.0, 1.0), vec4(1.0, 0.6, 0.0, 1.0), vec4(1.0, 0.7019607843137254, 0.0, 1.0), vec4(1.0, 0.8, 0.0, 1.0), vec4(1.0, 0.9019607843137255, 0.0, 1.0), vec4(1.0, 1.0, 0.0, 1.0)), x); }
vec4 winter(float x) { return get_color(vec4[11](vec4(0.0, 0.0, 1.0, 1.0), vec4(0.0, 0.09803921568627451, 0.9509803921568627, 1.0), vec4(0.0, 0.2, 0.9, 1.0), vec4(0.0, 0.2980392156862745, 0.8509803921568627, 1.0), vec4(0.0, 0.4, 0.8, 1.0), vec4(0.0, 0.5019607843137255, 0.7490196078431373, 1.0), vec4(0.0, 0.6, 0.7, 1.0), vec4(0.0, 0.7019607843137254, 0.6490196078431373, 1.0), vec4(0.0, 0.8, 0.6, 1.0), vec4(0.0, 0.9019607843137255, 0.5490196078431373, 1.0), vec4(0.0, 1.0, 0.5, 1.0)), x); }
vec4 cool(float x) { return get_color(vec4[11](vec4(0.0, 1.0, 1.0, 1.0), vec4(0.09803921568627451, 0.9019607843137255, 1.0, 1.0), vec4(0.2, 0.8, 1.0, 1.0), vec4(0.2980392156862745, 0.7019607843137254, 1.0, 1.0), vec4(0.4, 0.6, 1.0, 1.0), vec4(0.5019607843137255, 0.4980392156862745, 1.0, 1.0), vec4(0.6, 0.4, 1.0, 1.0), vec4(0.7019607843137254, 0.29803921568627456, 1.0, 1.0), vec4(0.8, 0.19999999999999996, 1.0, 1.0), vec4(0.9019607843137255, 0.0980392156862745, 1.0, 1.0), vec4(1.0, 0.0, 1.0, 1.0)), x); }
vec4 hot(float x) { return get_color(vec4[11](vec4(0.0416, 0.0, 0.0, 1.0), vec4(0.2989711013608711, 0.0, 0.0, 1.0), vec4(0.5666370467761772, 0.0, 0.0, 1.0), vec4(0.8240081481370484, 0.0, 0.0, 1.0), vec4(1.0, 0.09166747604035141, 0.0, 1.0), vec4(1.0, 0.359314099938117, 0.0, 1.0), vec4(1.0, 0.6166666229167378, 0.0, 1.0), vec4(1.0, 0.8843132468145034, 0.0, 1.0), vec4(1.0, 1.0, 0.21249921249921258, 1.0), vec4(1.0, 1.0, 0.6139702022054964, 1.0), vec4(1.0, 1.0, 1.0, 1.0)), x); }
vec4 jet(float x) { return get_color(vec4[11](vec4(0.0, 0.0, 0.5, 1.0), vec4(0.0, 0.0, 0.945632798573975, 1.0), vec4(0.0, 0.3, 1.0, 1.0), vec4(0.0, 0.692156862745098, 1.0, 1.0), vec4(0.16129032258064513, 1.0, 0.8064516129032259, 1.0), vec4(0.4901960784313725, 1.0, 0.4775458570524984, 1.0), vec4(0.8064516129032256, 1.0, 0.16129032258064513, 1.0), vec4(1.0, 0.7705156136528688, 0.0, 1.0), vec4(1.0, 0.40740740740740755, 0.0, 1.0), vec4(0.9456327985739753, 0.029774872912127992, 0.0, 1.0), vec4(0.5, 0.0, 0.0, 1.0)), x); }


uniform float time; // @@Time
uniform float cycleSpeed; // @@Slider min=0.0 max=10.0 default=1.0
uniform float hueSpread; // @@Slider min=0.0 max=10.0 default=2.0

struct BeatInfo {
	float beat;
	float bpm;
	float intensity;
	float confidence;
};
uniform BeatInfo beatInfo; // @@baaahs.BeatLink:BeatInfo

struct FixtureInfo {
	vec3 boundaryMin;
	vec3 boundaryMax;
};
uniform FixtureInfo fixtureInfo; // @@FixtureInfo

int getPanelID(FixtureInfo info) {
	float idX = (info.boundaryMin.x * (info.boundaryMin.x + info.boundaryMax.x));
	float idY = (info.boundaryMin.y * (info.boundaryMin.y + info.boundaryMax.y));
	float idZ = (info.boundaryMin.z * (info.boundaryMin.z + info.boundaryMax.z));
	int id = int(idX + idY + idZ);
	return id;
}

/* Linearly slide from min to max by varying x from 0 to 1 */
float linear(float min, float max, float x) {
	return min + (max - min) * x;
}

/* Replace with color palette of your choice */
vec4 COLOR(float x) {
	float colorMin = 0.2;
	float colorMax = 0.8;
	return plasma(linear(colorMin, colorMax, x));
}

/* Alternate color option: instead of color palettes, pick hsv values and vary to the beat */
vec3 hsv2rgb(vec3 c){ vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0); vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www); return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y); }
vec4 COLOR_HSV_EVOLVE(float x) {
	float hue = (cycleSpeed/20. * time) + (0.05 * hueSpread * x);
	float value = 1.0 - (0.3 * x);
	return vec4(hsv2rgb(vec3( hue, 1.0, value)), 1.0);
}

// @return color
// @param uvIn uv-coordinate
vec4 main(vec2 uvIn) {
	int panelID = getPanelID(fixtureInfo);
	int colorIndex = (int(beatInfo.beat) + panelID) % 4;
	bool use_hsv = true;
	if (use_hsv) {
		return COLOR_HSV_EVOLVE(0.33 * float(colorIndex));
	} else {
		return COLOR(0.33 * float(colorIndex));
	}
}
