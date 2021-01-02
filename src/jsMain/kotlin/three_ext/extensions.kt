package three_ext

import three.js.ArrayLike
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
