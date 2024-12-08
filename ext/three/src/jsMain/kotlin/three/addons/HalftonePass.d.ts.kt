package three.addons

import three.IUniform
import three.IUniform__0
import three.ShaderMaterial

external interface HalftonePassParameters {
    var shape: Number?
        get() = definedExternally
        set(value) = definedExternally
    var radius: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rotateR: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rotateB: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rotateG: Number?
        get() = definedExternally
        set(value) = definedExternally
    var scatter: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blending: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendingMode: Number?
        get() = definedExternally
        set(value) = definedExternally
    var greyscale: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var disable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$93` {
    var tDiffuse: IUniform__0
    var shape: IUniform<Number>
    var radius: IUniform<Number>
    var rotateR: IUniform<Number>
    var rotateG: IUniform<Number>
    var rotateB: IUniform<Number>
    var scatter: IUniform<Number>
    var width: IUniform<Number>
    var height: IUniform<Number>
    var blending: IUniform<Number>
    var blendingMode: IUniform<Number>
    var greyscale: IUniform<Boolean>
    var disable: IUniform<Boolean>
}

open external class HalftonePass(width: Number, height: Number, params: HalftonePassParameters) : Pass {
    open var uniforms: `T$93`
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
}