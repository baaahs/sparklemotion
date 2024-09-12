@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external open class VolumeSlice(volume: Volume, index: Number = definedExternally, axis: String = definedExternally) {
    open var index: Number
    open var axis: String
    open var canvas: HTMLCanvasElement
    open var canvasBuffer: HTMLCanvasElement
    open var ctx: CanvasRenderingContext2D
    open var ctxBuffer: CanvasRenderingContext2D
    open var mesh: Mesh__0
    open var geometryNeedsUpdate: Boolean
    open var sliceAccess: Number
    open var jLength: Number
    open var iLength: Number
    open var matrix: Matrix3
    open fun repaint()
    open fun updateGeometry()
}