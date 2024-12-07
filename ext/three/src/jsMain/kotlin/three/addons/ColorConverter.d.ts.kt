@file:JsQualifier("three.addons.ColorConverter")
package three.addons.ColorConverter

import three.Color
import three.HSL

external fun setHSV(color: Color, h: Number, s: Number, v: Number): Color

external fun getHSV(color: Color, target: HSL): HSL