@file:JsModule("three")
@file:JsNonModule
package three.js

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface MeshPhysicalMaterialParameters : MeshStandardMaterialParameters {
    var anisotropyRotation: Number?
        get() = definedExternally
        set(value) = definedExternally
    var anisotropyMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatRoughness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatRoughnessMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatNormalScale: Vector2?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoatNormalMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var ior: Number?
        get() = definedExternally
        set(value) = definedExternally
    var reflectivity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var iridescenceMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var iridescenceIOR: Number?
        get() = definedExternally
        set(value) = definedExternally
    var iridescenceThicknessRange: dynamic /* JsTuple<Number, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var iridescenceThicknessMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var sheenColor: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var sheenColorMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var sheenRoughness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sheenRoughnessMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var transmissionMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var thickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var thicknessMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var attenuationDistance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var attenuationColor: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var specularIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var specularIntensityMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var specularColor: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var specularColorMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var anisotropy: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clearcoat: Number?
        get() = definedExternally
        set(value) = definedExternally
    var iridescence: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dispersion: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sheen: Number?
        get() = definedExternally
        set(value) = definedExternally
    var transmission: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class MeshPhysicalMaterial(parameters: MeshPhysicalMaterialParameters = definedExternally) : MeshStandardMaterial {
    open val isMeshPhysicalMaterial: Boolean
//    override var defines: Json
    override var type: String
    open var anisotropyRotation: Number
    open var anisotropyMap: Texture?
    open var clearcoatMap: Texture?
    open var clearcoatRoughness: Number
    open var clearcoatRoughnessMap: Texture?
    open var clearcoatNormalScale: Vector2
    open var clearcoatNormalMap: Texture?
    open var ior: Number
    open var iridescenceMap: Texture?
    open var iridescenceIOR: Number
    open var iridescenceThicknessRange: dynamic /* JsTuple<Number, Number> */
    open var iridescenceThicknessMap: Texture?
    open var sheenColor: Color
    open var sheenColorMap: Texture?
    open var sheenRoughness: Number
    open var sheenRoughnessMap: Texture?
    open var transmissionMap: Texture?
    open var thickness: Number
    open var thicknessMap: Texture?
    open var attenuationDistance: Number
    open var attenuationColor: Color
    open var specularIntensity: Number
    open var specularIntensityMap: Texture?
    open var specularColor: Color
    open var specularColorMap: Texture?
}