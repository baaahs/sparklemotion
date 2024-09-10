package three.js

external interface Face {
    var a: Number
    var b: Number
    var c: Number
    var normal: Vector3
    var materialIndex: Number
}

external interface Intersection<TIntersected : Object3D__0> {
    var distance: Number
    var distanceToRay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var point: Vector3
    var index: Number?
        get() = definedExternally
        set(value) = definedExternally
    var face: Face?
        get() = definedExternally
        set(value) = definedExternally
    var faceIndex: Number?
        get() = definedExternally
        set(value) = definedExternally
    var `object`: TIntersected
    var uv: Vector2?
        get() = definedExternally
        set(value) = definedExternally
    var uv1: Vector2?
        get() = definedExternally
        set(value) = definedExternally
    var normal: Vector3?
        get() = definedExternally
        set(value) = definedExternally
    var instanceId: Number?
        get() = definedExternally
        set(value) = definedExternally
    var pointOnLine: Vector3?
        get() = definedExternally
        set(value) = definedExternally
    var batchId: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Intersection__0 : Intersection<Object3D__0>

external interface `T$19` {
    var threshold: Number
}

external interface RaycasterParameters {
    var Mesh: Any
    var Line: `T$19`
    var Line2: `T$19`?
        get() = definedExternally
        set(value) = definedExternally
    var LOD: Any
    var Points: `T$19`
    var Sprite: Any
}

open external class Raycaster(origin: Vector3 = definedExternally, direction: Vector3 = definedExternally, near: Number = definedExternally, far: Number = definedExternally) {
    open var ray: Ray
    open var near: Number
    open var far: Number
    open var camera: Camera
    open var layers: Layers
    open var params: RaycasterParameters
    open fun set(origin: Vector3, direction: Vector3)
    open fun setFromCamera(coords: Vector2, camera: Camera)
    open fun setFromXRController(controller: XRTargetRaySpace): Raycaster /* this */
    open fun <TIntersected : Object3D__0> intersectObject(obj: Object3D__0, recursive: Boolean = definedExternally, optionalTarget: Array<Intersection<TIntersected>> = definedExternally): Array<Intersection<TIntersected>>
    open fun <TIntersected : Object3D__0> intersectObjects(objects: Array<Object3D__0>, recursive: Boolean = definedExternally, optionalTarget: Array<Intersection<TIntersected>> = definedExternally): Array<Intersection<TIntersected>>
}