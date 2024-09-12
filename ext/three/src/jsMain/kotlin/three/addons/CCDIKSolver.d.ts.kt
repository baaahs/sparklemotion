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

external open class CCDIKSolver(mesh: SkinnedMesh__0, iks: Array<IK> = definedExternally) {
    open var mesh: SkinnedMesh__0
    open var iks: Array<IK>
    open fun update(): CCDIKSolver /* this */
    open fun updateOne(ik: IK): CCDIKSolver /* this */
    open fun createHelper(sphereSize: Number = definedExternally): CCDIKHelper
}

external open class CCDIKHelper(mesh: SkinnedMesh__0, iks: Array<IK> = definedExternally, sphereSize: Number = definedExternally) : Object3D__0 {
    open var root: SkinnedMesh__0
    open var iks: Array<IK>
    open var sphereGeometry: SphereGeometry
    open var targetSphereMaterial: MeshBasicMaterial
    open var effectorSphereMaterial: MeshBasicMaterial
    open var linkSphereMaterial: MeshBasicMaterial
    open var lineMaterial: LineBasicMaterial
    open fun dispose()
}