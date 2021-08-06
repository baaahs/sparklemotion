package kgl

import com.danielgergely.kgl.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

actual class IntBuffer private constructor(buffer: IntBuffer): Buffer(buffer) {
    actual constructor(buffer: Array<Int>) : this(alloc(buffer.size).also { it.put(buffer.toIntArray()) })
    actual constructor(buffer: IntArray) : this(alloc(buffer.size).also { it.put(buffer) })
    actual constructor(size: Int) : this(alloc(size))

    companion object {
        private fun alloc(size: Int) =
            ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer()
    }

    private val intBuffer: IntBuffer = buffer

    override val size: Int
        get() = intBuffer.capacity()

    override val sizeInBytes: Int
        get() = intBuffer.capacity() * 4

    actual fun put(f: Int) {
        intBuffer.put(f)
    }

    actual fun put(intArray: IntArray) = put(intArray, 0, intArray.size)

    actual fun put(intArray: IntArray, offset: Int, length: Int) {
        intBuffer.put(intArray, offset, length)
    }

    actual operator fun set(pos: Int, f: Int) {
        intBuffer.put(pos, f)
    }

    actual fun get(): Int = intBuffer.get()

    actual fun get(intArray: IntArray) {
        get(intArray, 0, intArray.size)
    }

    actual fun get(intArray: IntArray, offset: Int, length: Int) {
        intBuffer.get(intArray, offset, length)
    }

    actual operator fun get(pos: Int): Int = intBuffer.get(pos)
}
