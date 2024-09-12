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

external interface `T$74` {
    var data: Vector2
}

external interface InteractiveObject3DEventMap : Object3DEventMap {
    var hoveron: `T$74`
    var pointerdown: `T$74`
    var pointerup: `T$74`
    var pointermove: `T$74`
    var mousedown: `T$74`
    var mouseup: `T$74`
    var mousemove: `T$74`
    var click: `T$74`
}

external open class InteractiveObject3D : Object3D<InteractiveObject3DEventMap>

external open class InteractiveGroup : Group__0 {
    open fun listenToPointerEvents(renderer: WebGLRenderer, camera: Camera)
    open fun listenToXRControllerEvents(controller: XRTargetRaySpace)
}