@file:JsModule("three")
@file:JsNonModule
package three

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.Intersect
import info.laht.threekt.core.Object3D
import info.laht.threekt.math.Ray
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3

// info.laht.threekt.core.baaahs.Raycaster has some problems...
open external class Raycaster {

    constructor(
        origin: Vector3 = definedExternally,
        direction: Vector3 = definedExternally,
        near: Number = definedExternally,
        far: Number = definedExternally
    )

    var ray: Ray
    var near: Double
    var far: Double

    fun set(origin: Vector3, direction: Vector3): Raycaster

    fun setFromCamera(coord: Vector2, camera: Camera)

    fun intersectObject(object3D: Object3D, recursive: Boolean): Array<Intersect>

    fun intersectObjects(objects: Array<Object3D>, recursive: Boolean): Array<Intersect>

}