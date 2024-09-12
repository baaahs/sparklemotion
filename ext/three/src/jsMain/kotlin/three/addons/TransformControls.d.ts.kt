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

external interface `T$61` {
    var mode: String /* "translate" | "rotate" | "scale" */
}

external interface TransformControlsEventMap : Object3DEventMap {
    @nativeGetter
    operator fun get(key: String): `T$54`?
    @nativeSetter
    operator fun set(key: String, value: `T$54`)
    var change: Any
    var mouseDown: `T$61`
    var mouseUp: `T$61`
    var objectChange: Any
}

external open class TransformControls(obj: Camera, domElement: HTMLElement = definedExternally) : Object3D<TransformControlsEventMap> {
    open var domElement: HTMLElement
    open var camera: Camera
    open var `object`: Object3D__0?
    open var enabled: Boolean
    open var axis: String /* "X" | "Y" | "Z" | "E" | "XY" | "YZ" | "XZ" | "XYZ" | "XYZE" */
    open var mode: String /* "translate" | "rotate" | "scale" */
    open var translationSnap: Number?
    open var rotationSnap: Number?
    open var space: String /* "world" | "local" */
    open var size: Number
    open var dragging: Boolean
    open var showX: Boolean
    open var showY: Boolean
    open var showZ: Boolean
    open val isTransformControls: Boolean
    open var mouseButtons: `T$56`
    open fun pointerHover(pointer: PointerEvent?)
    open fun pointerDown(pointer: PointerEvent?)
    open fun pointerMove(pointer: PointerEvent?)
    open fun pointerUp(pointer: PointerEvent?)
    open fun attach(obj: Object3D__0): TransformControls /* this */
    open fun detach(): TransformControls /* this */
    open fun getMode(): String /* "translate" | "rotate" | "scale" */
    open fun getRaycaster(): Raycaster
    open fun setMode(mode: String /* "translate" | "rotate" | "scale" */)
    open fun setTranslationSnap(translationSnap: Number?)
    open fun setRotationSnap(rotationSnap: Number?)
    open fun setScaleSnap(scaleSnap: Number?)
    open fun setSize(size: Number)
    open fun setSpace(space: String /* "world" | "local" */)
    open fun reset()
    open fun dispose()
}

external interface `T$62` {
    var translate: Object3D__0
    var rotate: Object3D__0
    var scale: Object3D__0
}

external open class TransformControlsGizmo : Object3D__0 {
    override var type: String /* "TransformControlsGizmo" */
    open var isTransformControlsGizmo: Boolean
    open var gizmo: `T$62`
    open var helper: `T$62`
    open var picker: `T$62`
}

external open class TransformControlsPlane : Mesh__0 {
    override var type: String /* "TransformControlsPlane" */
    open var isTransformControlsPlane: Boolean
    open var mode: String /* "translate" | "rotate" | "scale" */
    open var axis: String /* "X" | "Y" | "Z" | "XY" | "YZ" | "XZ" | "XYZ" | "E" */
    open var space: String /* "local" | "world" */
    open var eye: Vector3
    open var worldPosition: Vector3
    open var worldQuaternion: Quaternion
}