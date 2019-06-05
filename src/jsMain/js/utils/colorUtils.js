import chroma from 'chroma-js';

export function xy2polar(x, y) {
  let r = Math.sqrt(x * x + y * y);
  let phi = Math.atan2(y, x);
  return [r, phi];
}

export function polar2xy(r, phi) {
  const x = Math.cos(phi) * r;
  const y = Math.sin(phi) * r;
  return [x, y];
}

// rad in [-π, π] range
// return degree in [0, 360] range
export function rad2deg(rad) {
  return ((rad + Math.PI) / (2 * Math.PI)) * 360;
}
// degree in [0, 360] range
// return rad in [-π, π] range
export function deg2rad(deg) {
  return ((deg / 360) * (2 * Math.PI)) - Math.PI
}

export function xy2rgb(x, y, radius) {
  let [r, phi] = xy2polar(x, y);

  let deg = rad2deg(phi);
  let hue = deg;
  let saturation = r / radius;
  let value = 1.0;

  let [red, green, blue] = chroma.hsv(hue, saturation, value).rgb();
  return [red, green, blue];
}

export function rgb2xy(rgb, radius) {
  // Convert the color to polar coordinates
  const [hue, saturation] = chroma.rgb(...rgb).hsv();
  const deg = hue || 0; // hue will be NaN for white :-/
  const phi = deg2rad(deg);
  const r = saturation * radius;
  const [x, y] = polar2xy(r, phi);
  return [x + radius, y + radius];
}
