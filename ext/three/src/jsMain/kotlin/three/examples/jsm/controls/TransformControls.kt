@file:JsModule("three/examples/jsm/controls/TransformControls")
@file:JsNonModule
package three.examples.jsm.controls

import org.w3c.dom.events.MouseEvent
import three.js.Camera
import three.js.Object3D
import web.html.HTMLElement

open external class TransformControls(camera: Camera, domElement: HTMLElement) : Object3D {
    var `object`: Object3D?
    var enabled: Boolean
    var mode: String
    var translationSnap: Double?
    var rotationSnap: Double?
    var scaleSnap: Double?
    var space: String
    var size: Double
    var dragging: Boolean

    //    fun updateMatrixWorld()
    fun pointerHover(pointer: MouseEvent)
    fun pointerDown(pointer: MouseEvent)
    fun pointerMove(pointer: MouseEvent)
    fun pointerUp(pointer: MouseEvent)
    fun dispose()
    fun detach()
    fun reset()
    fun setTranslationSnap(translationSnap: Double)
    fun setRotationSnap(rotationSnap: Double)
    fun setScaleSnap(scaleSnap: Double)
    fun setSize(size: Double)
    fun setSpace(space: String)
    fun update()
}