@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import three.*

external interface `T$55` {
    var `object`: Object3D
}

external interface DragControlsEventMap {
    var hoveron: `T$55`
    var hoveroff: `T$55`
    var dragstart: `T$55`
    var drag: `T$55`
    var dragend: `T$55`
}

external interface `T$56` {
    var LEFT: MOUSE?
        get() = definedExternally
        set(value) = definedExternally
    var MIDDLE: MOUSE?
        get() = definedExternally
        set(value) = definedExternally
    var RIGHT: MOUSE?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$57` {
    var ONE: TOUCH?
        get() = definedExternally
        set(value) = definedExternally
}

open external class DragControls(objects: Array<Object3D>, camera: Camera, domElement: HTMLElement? = definedExternally) : Controls {
    open var objects: Array<Object3D>
    open var recursive: Boolean
    open var transformGroup: Boolean
    open var rotateSpeed: Number
    open var raycaster: Raycaster
    open var mouseButtons: `T$56`
    open var touches: `T$57`
    override fun connect()
    override fun disconnect()
    override fun dispose()
    open fun getRaycaster(): Raycaster
    open fun setObjects(objects: Array<Object3D>)
    open fun getObjects(): Array<Object3D>
    open fun activate()
    open fun deactivate()
}