package three.addons

import three.*

external interface MMDPhysicsParameter {
    var unitStep: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxStepNum: Number?
        get() = definedExternally
        set(value) = definedExternally
    var gravity: Vector3?
        get() = definedExternally
        set(value) = definedExternally
}

open external class MMDPhysics(mesh: SkinnedMesh<*, *>, rigidBodyParams: Array<Any?>, constraintParams: Array<Any?> = definedExternally, params: MMDPhysicsParameter = definedExternally) {
    open var manager: ResourceManager
    open var mesh: SkinnedMesh<*, *>
    open var unitStep: Number
    open var maxStepNum: Number
    open var gravity: Vector3
    open var world: Any?
    open var bodies: Array<RigidBody>
    open var constraints: Array<Constraint>
    open fun update(delta: Number): MMDPhysics /* this */
    open fun reset(): MMDPhysics /* this */
    open fun warmup(cycles: Number): MMDPhysics /* this */
    open fun setGravity(gravity: Vector3): MMDPhysics /* this */
    open fun createHelper(): MMDPhysicsHelper
}

open external class ResourceManager {
    open var threeVector3s: Array<Vector3>
    open var threeMatrix4s: Array<Matrix4>
    open var threeQuaternions: Array<Quaternion>
    open var threeEulers: Array<Euler>
    open var transforms: Array<Any?>
    open var quaternions: Array<Any?>
    open var vector3s: Array<Any?>
    open fun allocThreeVector3()
    open fun freeThreeVector3(v: Vector3)
    open fun allocThreeMatrix4()
    open fun freeThreeMatrix4(m: Matrix4)
    open fun allocThreeQuaternion()
    open fun freeThreeQuaternion(q: Quaternion)
    open fun allocThreeEuler()
    open fun freeThreeEuler(e: Euler)
    open fun allocTransform()
    open fun freeTransform(t: Any?)
    open fun allocQuaternion()
    open fun freeQuaternion(q: Any?)
    open fun allocVector3()
    open fun freeVector3(v: Any?)
    open fun setIdentity()
    open fun getBasis(t: Any?): Any?
    open fun getBasisAsMatrix3(t: Any?): Any?
    open fun getOrigin(t: Any?): Any?
    open fun setOrigin(t: Any?, v: Any?)
    open fun copyOrigin(t1: Any?, t2: Any?)
    open fun setBasis(t: Any?, q: Any?)
    open fun setBasisFromMatrix3(t: Any?, m: Any?)
    open fun setOriginFromArray3(t: Any?, a: Array<Number>)
    open fun setOriginFromThreeVector3(t: Any?, v: Vector3)
    open fun setBasisFromArray3(t: Any?, a: Array<Number>)
    open fun setBasisFromThreeQuaternion(t: Any?, a: Quaternion)
    open fun multiplyTransforms(t1: Any?, t2: Any?): Any?
    open fun inverseTransform(t: Any?): Any?
    open fun multiplyMatrices3(m1: Any?, m2: Any?): Any?
    open fun addVector3(v1: Any?, v2: Any?): Any?
    open fun dotVectors3(v1: Any?, v2: Any?): Number
    open fun rowOfMatrix3(m: Any?, i: Number): Any?
    open fun columnOfMatrix3(m: Any?, i: Number): Any?
    open fun negativeVector3(v: Any?): Any?
    open fun multiplyMatrix3ByVector3(m: Any?, v: Any?): Any?
    open fun transposeMatrix3(m: Any?): Any?
    open fun quaternionToMatrix3(q: Any?): Any?
    open fun matrix3ToQuaternion(m: Any?): Any?
}

open external class RigidBody(mesh: SkinnedMesh<*, *>, world: Any?, params: Any?, manager: ResourceManager) {
    open var mesh: SkinnedMesh<*, *>
    open var world: Any?
    open var params: Any?
    open var manager: ResourceManager
    open var body: Any?
    open var bone: Bone__0
    open var boneOffsetForm: Any?
    open var boneOffsetFormInverse: Any?
    open fun reset(): RigidBody /* this */
    open fun updateFromBone(): RigidBody /* this */
    open fun updateBone(): RigidBody /* this */
}

open external class Constraint(mesh: SkinnedMesh<*, *>, world: Any?, bodyA: RigidBody, bodyB: RigidBody, params: Any?, manager: ResourceManager) {
    open var mesh: SkinnedMesh<*, *>
    open var world: Any?
    open var bodyA: RigidBody
    open var bodyB: RigidBody
    open var params: Any?
    open var manager: ResourceManager
}

open external class MMDPhysicsHelper(mesh: SkinnedMesh<*, *>, physics: MMDPhysics) : Object3D {
    open var mesh: SkinnedMesh<*, *>
    open var physics: MMDPhysics
    open var materials: dynamic /* JsTuple<MeshBasicMaterial, MeshBasicMaterial, MeshBasicMaterial> */
    open fun dispose()
}