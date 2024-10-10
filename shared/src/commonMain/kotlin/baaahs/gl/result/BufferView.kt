package baaahs.gl.result

interface BufferView<T> {
    operator fun set(componentIndex: Int, t: T)
    operator fun set(componentIndex: Int, index: Int, t: T)
    operator fun get(componentIndex: Int): T
    operator fun get(componentIndex: Int, index: Int): T
}
