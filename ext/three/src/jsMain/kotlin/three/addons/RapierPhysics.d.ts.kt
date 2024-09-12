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

external interface Vector {
    var x: Number
    var y: Number
    var z: Number
}

external interface RapierPhysicsObject {
    var addScene: (scene: Object3D__0) -> Unit
    var addMesh: (mesh: Mesh__0, mass: Number, restitution: Number) -> Unit
    var setMeshPosition: (mesh: Mesh__0, position: Vector, index: Number) -> Unit
    var setMeshVelocity: (mesh: Mesh__0, velocity: Vector, index: Number) -> Unit
}

external fun RapierPhysics(): Promise<RapierPhysicsObject>