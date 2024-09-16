@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import three.Matrix3
import three.Mesh

open external class VolumeSlice(volume: Volume, index: Number = definedExternally, axis: String = definedExternally) {
    open var index: Number
    open var axis: String
    open var canvas: HTMLCanvasElement
    open var canvasBuffer: HTMLCanvasElement
    open var ctx: CanvasRenderingContext2D
    open var ctxBuffer: CanvasRenderingContext2D
    open var mesh: Mesh<*, *>
    open var geometryNeedsUpdate: Boolean
    open var sliceAccess: Number
    open var jLength: Number
    open var iLength: Number
    open var matrix: Matrix3
    open fun repaint()
    open fun updateGeometry()
}