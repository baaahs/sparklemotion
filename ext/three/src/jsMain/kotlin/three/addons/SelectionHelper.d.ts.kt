@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import three.Vector2
import three.WebGLRenderer

open external class SelectionHelper(renderer: WebGLRenderer, cssClassName: String) {
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