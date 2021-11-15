package three_ext

import three.js.ArrayLike
import three.js.Euler
import three.js.Vector3

operator fun <T> ArrayLike<T>.set(i: Int, t: T) {
    this.asDynamic()[i] = t
}

operator fun Vector3.plus(other: Vector3): Vector3 {
    return this.clone().add(other)
}

operator fun Vector3.minus(other: Vector3): Vector3 {
    return this.clone().sub(other)
}

fun Vector3.set(other: Vector3) {
    set(other.x, other.y, other.z)
}

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