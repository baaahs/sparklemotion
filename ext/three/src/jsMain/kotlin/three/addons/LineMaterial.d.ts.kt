package three.addons

import three.Color
import three.ShaderMaterial
import three.ShaderMaterialParameters
import three.Vector2

external interface LineMaterialParameters : ShaderMaterialParameters {
    override var alphaToCoverage: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var dashed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var dashScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dashSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dashOffset: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gapSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var resolution: Vector2?
        get() = definedExternally
        set(value) = definedExternally
    var worldUnits: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class LineMaterial(parameters: LineMaterialParameters = definedExternally) : ShaderMaterial {
    open var color: Color
    open var dashed: Boolean
    open var dashScale: Number
    open var dashSize: Number
    open var dashOffset: Number
    open var gapSize: Number
    override var opacity: Number
    open val isLineMaterial: Boolean
    override var linewidth: Number
    open var resolution: Vector2
    override var alphaToCoverage: Boolean
    open var worldUnits: Boolean
}