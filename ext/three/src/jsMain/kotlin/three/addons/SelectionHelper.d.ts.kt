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

external open class SelectionHelper(renderer: WebGLRenderer, cssClassName: String) {
    open var element: HTMLElement
    open var isDown: Boolean
    open var enabled: Boolean
    open var pointBottomRight: Vector2
    open var pointTopLeft: Vector2
    open var renderer: WebGLRenderer
    open var startPoint: Vector2
    open fun onSelectStart(event: Event)
    open fun onSelectMove(event: Event)
    open fun onSelectOver(event: Event)
    open fun dispose()
}