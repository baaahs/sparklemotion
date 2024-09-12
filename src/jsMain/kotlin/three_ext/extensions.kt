package three_ext

import baaahs.geom.Vector3F
import baaahs.visualizer.toVector3
import three.Euler
import three.Object3D
import three.Vector3

operator fun Vector3.plus(other: Vector3): Vector3 {
    return this.clone().add(other)
}

operator fun Vector3.minus(other: Vector3): Vector3 {
    return this.clone().sub(other)
}

fun Vector3.set(other: Vector3) {
    set(other.x, other.y, other.z)
}

fun Vector3.set(other: Vector3F) {
    set(other.x, other.y, other.z)
}

fun Vector3.toVector3F(): Vector3F =
    Vector3F(x.toFloat(), y.toFloat(), z.toFloat())


val vector3FacingForward get() =
    Vector3F.facingForward.toVector3()

operator fun Euler.plus(other: Euler): Euler = Euler(
    x.toDouble() + other.x.toDouble(),
    y.toDouble() + other.y.toDouble(),
    z.toDouble() + other.z.toDouble()
)

operator fun Euler.minus(other: Euler): Euler = Euler(
    x.toDouble() - other.x.toDouble(),
    y.toDouble() - other.y.toDouble(),
    z.toDouble() - other.z.toDouble()
)

fun Euler.set(other: Euler) {
    set(other.x, other.y, other.z)
}

fun Object3D.clear() {
    while (children.isNotEmpty()) {
        remove(children[0])
    }
}