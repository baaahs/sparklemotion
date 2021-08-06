package kgl

import com.danielgergely.kgl.Buffer

expect class IntBuffer : Buffer {
    constructor(buffer: Array<Int>)
    constructor(buffer: IntArray)
    constructor(size: Int)

    fun put(f: Int)
    fun put(intArray: IntArray)
    fun put(intArray: IntArray, offset: Int, length: Int)
    operator fun set(pos: Int, f: Int)

    fun get(): Int
    fun get(intArray: IntArray)
    fun get(intArray: IntArray, offset: Int, length: Int)
    operator fun get(pos: Int): Int
}
