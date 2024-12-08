package three.addons

import three.Light__0
import three.Renderer

external interface Size {
    var width: Number
    var height: Number
    var set: (width: Number, height: Number) -> Unit
}

external interface Position {
    var x: Number
    var y: Number
    var set: (x: Number, y: Number) -> Unit
}

open external class ShadowMapViewer(light: Light__0) {
    open var enabled: Boolean
    open var size: Size
    open var position: Position
    open fun render(renderer: Renderer)
    open fun updateForWindowResize()
    open fun update()
}