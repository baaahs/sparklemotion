package three.addons

import three.*

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

open external class InteractiveObject3D : Object3D

open external class InteractiveGroup : Group {
    open fun listenToPointerEvents(renderer: WebGLRenderer, camera: Camera)
    open fun listenToXRControllerEvents(controller: XRTargetRaySpace)
}