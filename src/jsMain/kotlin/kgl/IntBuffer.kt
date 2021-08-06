package kgl

import com.danielgergely.kgl.Buffer
import org.khronos.webgl.Int32Array
import org.khronos.webgl.get
import org.khronos.webgl.set

actual class IntBuffer constructor(buffer: Int32Array) : Buffer(buffer) {
    actual constructor(buffer: Array<Int>) : this(Int32Array(buffer))
    actual constructor(buffer: IntArray) : this(Int32Array(buffer.toTypedArray()))
    actual constructor(size: Int) : this(IntArray(size))

    private val intBuffer: Int32Array = buffer

    override val size: Int
        get() = intBuffer.length

    override val sizeInBytes: Int
        get() = buffer.byteLength

    actual fun put(f: Int) {
        intBuffer[position] = f
        position += 1
    }

    actual fun put(intArray: IntArray) = put(intArray, 0, intArray.size)

    actual fun put(intArray: IntArray, offset: Int, length: Int) {
        intBuffer.set((intArray.unsafeCast<Int32Array>()).subarray(offset, length), position)
        position += length
    }

    actual operator fun set(pos: Int, f: Int) {
        intBuffer[pos] = f
    }

    actual fun get(): Int = intBuffer[position]

    actual fun get(intArray: IntArray) {
        get(intArray, 0, intArray.size)
    }

    actual fun get(intArray: IntArray, offset: Int, length: Int) {
        val dest = intArray.unsafeCast<Int32Array>()
        dest.subarray(offset, length).set(intBuffer, position)
    }

    actual operator fun get(pos: Int): Int = intBuffer[pos]
}
