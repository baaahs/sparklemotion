package three.addons

import three.Mesh
import three.Object3D
import kotlin.js.Promise

external interface Vector {
    var x: Number
    var y: Number
    var z: Number
}

external interface RapierPhysicsObject {
    var addScene: (scene: Object3D) -> Unit
    var addMesh: (mesh: Mesh<*, *>, mass: Number, restitution: Number) -> Unit
    var setMeshPosition: (mesh: Mesh<*, *>, position: Vector, index: Number) -> Unit
    var setMeshVelocity: (mesh: Mesh<*, *>, velocity: Vector, index: Number) -> Unit
}

external fun RapierPhysics(): Promise<RapierPhysicsObject>