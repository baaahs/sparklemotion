@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

external interface `T$35` {
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var index: Number
    var limitation: Vector3?
        get() = definedExternally
        set(value) = definedExternally
    var rotationMin: Vector3?
        get() = definedExternally
        set(value) = definedExternally
    var rotationMax: Vector3?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IK {
    var effector: Number
    var iteration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var links: Array<`T$35`>
    var minAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxAngle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var target: Number
}

open external class CCDIKSolver(mesh: SkinnedMesh<*, *>, iks: Array<IK> = definedExternally) {
    open var mesh: SkinnedMesh<*, *>
    open var iks: Array<IK>
    open fun update(): CCDIKSolver /* this */
    open fun updateOne(ik: IK): CCDIKSolver /* this */
    open fun createHelper(sphereSize: Number = definedExternally): CCDIKHelper
}

open external class CCDIKHelper(mesh: SkinnedMesh<*, *>, iks: Array<IK> = definedExternally, sphereSize: Number = definedExternally) : Object3D {
    open var root: SkinnedMesh<*, *>
    open var iks: Array<IK>
    open var sphereGeometry: SphereGeometry
    open var targetSphereMaterial: MeshBasicMaterial
    open var effectorSphereMaterial: MeshBasicMaterial
    open var linkSphereMaterial: MeshBasicMaterial
    open var lineMaterial: LineBasicMaterial
    open fun dispose()
}