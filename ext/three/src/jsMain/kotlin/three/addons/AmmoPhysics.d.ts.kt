package three.addons

import three.Mesh
import three.Object3D
import three.Vector3
import kotlin.js.Promise

external interface AmmoPhysicsObject {
    var addScene: (scene: Object3D) -> Unit
    var addMesh: (mesh: Mesh<*, *>, mass: Number) -> Unit
    var setMeshPosition: (mesh: Mesh<*, *>, position: Vector3, index: Number) -> Unit
}

external fun AmmoPhysics(): Promise<AmmoPhysicsObject>