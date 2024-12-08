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