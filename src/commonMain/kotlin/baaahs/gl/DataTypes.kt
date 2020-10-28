package baaahs.gl

import kotlinx.serialization.Serializable

@Serializable
data class Vec3(val x: Int, val y: Int, val z: Int) {
    constructor(v: Int) : this(v, v, v)
}

@Serializable
data class Vec4(val x: Int, val y: Int, val z: Int, val w: Int) {
    constructor(v: Int) : this(v, v, v, v)
}