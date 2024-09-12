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

external open class SVGObject(node: SVGElement) : Object3D__0 {
    open var node: SVGElement
}

external interface `T$96` {
    var vertices: Number
    var faces: Number
}

external interface `T$97` {
    var render: `T$96`
}

external open class SVGRenderer {
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