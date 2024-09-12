@file:JsQualifier("three.addons.ColorConverter")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons.ColorConverter

import three.Color
import three.HSL

external fun setHSV(color: Color, h: Number, s: Number, v: Number): Color

external fun getHSV(color: Color, target: HSL): HSL