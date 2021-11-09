package baaahs.glsl

interface Uniform<T> {
    val exists: Boolean

    fun set(value: T)
}