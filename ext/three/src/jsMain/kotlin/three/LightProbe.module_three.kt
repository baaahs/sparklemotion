@file:JsModule("three")
@file:JsNonModule
package three

open external class LightProbe(sh: SphericalHarmonics3 = definedExternally, intensity: Number = definedExternally) : Light__0 {
    open val isLightProbe: Boolean
    open var sh: SphericalHarmonics3
    open fun fromJSON(json: Any): LightProbe
}