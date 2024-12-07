package three.addons

import org.w3c.dom.svg.SVGElement
import three.Camera
import three.Color
import three.Object3D
import three.Scene

open external class SVGObject(node: SVGElement) : Object3D {
    open var node: SVGElement
}

external interface `T$96` {
    var vertices: Number
    var faces: Number
}

external interface `T$97` {
    var render: `T$96`
}

open external class SVGRenderer {
    open var domElement: SVGElement
    open var autoClear: Boolean
    open var sortObjects: Boolean
    open var sortElements: Boolean
    open var overdraw: Number
    open var outputColorSpace: Any
    open var info: `T$97`
    open fun getSize(): `T$95`
    open fun setQuality(quality: String)
    open fun setClearColor(color: Color, alpha: Number)
    open fun setPixelRatio()
    open fun setSize(width: Number, height: Number)
    open fun setPrecision(precision: Number)
    open fun clear()
    open fun render(scene: Scene, camera: Camera)
}