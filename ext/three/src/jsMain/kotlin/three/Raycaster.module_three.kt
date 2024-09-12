@file:JsModule("three")
@file:JsNonModule
package three

external interface Face {
    var a: Int
    var b: Int
    var c: Int
    var normal: Vector3
    var materialIndex: Int
}

external interface Intersection<TIntersected : Object3D/*<Object3DEventMap>*/> {
    var distance: Double
    var distanceToRay: Double?
        get() = definedExternally
        set(value) = definedExternally
    var point: Vector3
    var index: Int?
        get() = definedExternally
        set(value) = definedExternally
    var face: Face?
        get() = definedExternally
        set(value) = definedExternally
    var faceIndex: Int?
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
    var instanceId: Int?
        get() = definedExternally
        set(value) = definedExternally
    var pointOnLine: Vector3?
        get() = definedExternally
        set(value) = definedExternally
    var batchId: Int?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Intersection__0 : Intersection<Object3D/*<Object3DEventMap>*/>

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

open external class Raycaster(origin: Vector3 = definedExternally, direction: Vector3 = definedExternally, near: Double = definedExternally, far: Double = definedExternally) {
    open var ray: Ray
    open var near: Double
    open var far: Double
    open var camera: Camera
    open var layers: Layers
    open var params: RaycasterParameters
    open fun set(origin: Vector3, direction: Vector3)
    open fun setFromCamera(coords: Vector2, camera: Camera)
    open fun setFromXRController(controller: XRTargetRaySpace): Raycaster /* this */
    open fun intersectObject(obj: Object3D/*<Object3DEventMap>*/, recursive: Boolean = definedExternally, optionalTarget: Array<Intersection<out Object3D>> = definedExternally): Array<Intersection<out Object3D>>
    open fun intersectObjects(objects: Array<Object3D/*<Object3DEventMap>*/>, recursive: Boolean = definedExternally, optionalTarget: Array<Intersection<out Object3D>> = definedExternally): Array<Intersection<out Object3D>>
}