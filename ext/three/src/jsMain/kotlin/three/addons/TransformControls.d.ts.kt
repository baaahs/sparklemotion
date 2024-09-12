@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import three.*
import web.uievents.PointerEvent

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

open external class TransformControls(obj: Camera, domElement: HTMLElement = definedExternally) : Object3D {
    open var domElement: HTMLElement
    open var camera: Camera
    open var `object`: Object3D?
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
    override fun attach(obj: Object3D): TransformControls /* this */
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
    var translate: Object3D
    var rotate: Object3D
    var scale: Object3D
}

open external class TransformControlsGizmo : Object3D {
    override var type: String /* "TransformControlsGizmo" */
    open var isTransformControlsGizmo: Boolean
    open var gizmo: `T$62`
    open var helper: `T$62`
    open var picker: `T$62`
}

open external class TransformControlsPlane : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    override var type: String /* "TransformControlsPlane" */
    open var isTransformControlsPlane: Boolean
    open var mode: String /* "translate" | "rotate" | "scale" */
    open var axis: String /* "X" | "Y" | "Z" | "XY" | "YZ" | "XZ" | "XYZ" | "E" */
    open var space: String /* "local" | "world" */
    open var eye: Vector3
    open var worldPosition: Vector3
    open var worldQuaternion: Quaternion
}