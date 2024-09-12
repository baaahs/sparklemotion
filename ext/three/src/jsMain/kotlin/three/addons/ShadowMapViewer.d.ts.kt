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

external open class ShadowMapViewer(light: Light__0) {
    open var enabled: Boolean
    open var size: Size
    open var position: Position
    open fun render(renderer: Renderer)
    open fun updateForWindowResize()
    open fun update()
}