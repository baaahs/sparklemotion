@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class LightProbe(sh: SphericalHarmonics3 = definedExternally, intensity: Number = definedExternally) : Light {
    override var type: String
    open var isLightProbe: Boolean
    open var sh: SphericalHarmonics3
    open fun fromJSON(json: Any?): LightProbe
}