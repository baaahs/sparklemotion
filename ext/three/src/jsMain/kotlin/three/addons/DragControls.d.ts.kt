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

external interface `T$55` {
    var `object`: Object3D__0
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

external open class DragControls(objects: Array<Object3D__0>, camera: Camera, domElement: HTMLElement? = definedExternally) : Controls<DragControlsEventMap> {
    open var objects: Array<Object3D__0>
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
    open fun setObjects(objects: Array<Object3D__0>)
    open fun getObjects(): Array<Object3D__0>
    open fun activate()
    open fun deactivate()
}