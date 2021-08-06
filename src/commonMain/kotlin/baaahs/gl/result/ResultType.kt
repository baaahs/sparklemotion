package baaahs.gl.result

import baaahs.gl.GlContext

interface ResultType<T : ResultBuffer> {
    val renderPixelFormat: Int
    val readPixelFormat: Int
    val readType: Int
    val stride: Int

    fun createResultBuffer(gl: GlContext, index: Int): T
}