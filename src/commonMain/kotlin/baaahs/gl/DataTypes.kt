package baaahs.gl

import kotlinx.serialization.Serializable


@Serializable
data class Vec2(val x: Int, val y: Int) {
    constructor(v: Int) : this(v, v)
}

@Serializable
data class Vec3(val x: Int, val y: Int, val z: Int) {
    constructor(v: Int) : this(v, v, v)
}

@Serializable
data class Vec4(val x: Int, val y: Int, val z: Int, val w: Int) {
    constructor(v: Int) : this(v, v, v, v)
}

@Serializable
data class Vec2F(val x: Float, val y: Float) {
    constructor(v: Float) : this(v, v)
}

@Serializable
data class Vec3F(val x: Float, val y: Float, val z: Float) {
    constructor(v: Float) : this(v, v, v)
}

@Serializable
data class Vec4F(val x: Float, val y: Float, val z: Float, val w: Float) {
    constructor(v: Float) : this(v, v, v, v)
}

