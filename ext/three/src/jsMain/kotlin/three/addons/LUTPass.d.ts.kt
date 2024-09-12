@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

external interface LUTPassParameters {
    var lut: dynamic /* DataTexture? | Data3DTexture? */
        get() = definedExternally
        set(value) = definedExternally
    var intensity: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LUTPass(params: LUTPassParameters) : ShaderPass {
    open var lut: dynamic /* DataTexture | Data3DTexture */
    open var intensity: Number
}