@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

external interface Intersection {
    var distance: Number
    var distanceToRay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var point: Vector3
    var index: Number?
        get() = definedExternally
        set(value) = definedExternally
    var face: Face3?
        get() = definedExternally
        set(value) = definedExternally
    var faceIndex: Number?
        get() = definedExternally
        set(value) = definedExternally
    var `object`: Object3D
    var uv: Vector2?
        get() = definedExternally
        set(value) = definedExternally
    var instanceId: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$21` {
    var threshold: Number
}

external interface RaycasterParameters {
    var Mesh: Any?
        get() = definedExternally
        set(value) = definedExternally
    var Line: `T$21`?
        get() = definedExternally
        set(value) = definedExternally
    var LOD: Any?
        get() = definedExternally
        set(value) = definedExternally
    var Points: `T$21`?
        get() = definedExternally
        set(value) = definedExternally
    var Sprite: Any?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Raycaster(origin: Vector3 = definedExternally, direction: Vector3 = definedExternally, near: Number = definedExternally, far: Number = definedExternally) {
    open var ray: Ray
    open var near: Number
    open var far: Number
    open var camera: Camera
    open var layers: Layers
    open var params: RaycasterParameters
    open fun set(origin: Vector3, direction: Vector3)
    open fun setFromCamera(coords: `T$3`, camera: Camera)
    open fun intersectObject(obj: Object3D, recursive: Boolean = definedExternally, optionalTarget: Array<Intersection> = definedExternally): Array<Intersection>
    open fun intersectObjects(objects: Array<Object3D>, recursive: Boolean = definedExternally, optionalTarget: Array<Intersection> = definedExternally): Array<Intersection>
}