package baaahs.gl.result

interface BufferView<T> {
    operator fun set(pixelIndex: Int, t: T)
    operator fun set(pixelIndex: Int, index: Int, t: T)
    operator fun get(pixelIndex: Int): T
    operator fun get(pixelIndex: Int, index: Int): T
}
