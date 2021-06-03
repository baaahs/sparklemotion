package baaahs.gl.render

import baaahs.gl.GlContext
import baaahs.util.Logger
import com.danielgergely.kgl.*

class Quad(private val gl: GlContext, rects: List<Rect>) {
    private val vertices = rects.flatMap { rect ->
        listOf(
            // First triangle:
            rect.right, rect.top,
            rect.left, rect.top,
            rect.left, rect.bottom,
            // Second triangle:
            rect.left, rect.bottom,
            rect.right, rect.bottom,
            rect.right, rect.top
        )
    }.toFloatArray()

    private var vao: VertexArrayObject = gl.check { createVertexArray() }
    private var quadVertexBuffer: GlBuffer = gl.check { createBuffers(1)[0] }
    private val sourceData = bufferOf(vertices)
    private var released = false

    init {
        gl.check { bindVertexArray(vao) }
        gl.check { bindBuffer(GL_ARRAY_BUFFER, quadVertexBuffer) }
        println("Quad: bind vertices (init)")
        println(vertices.map { it })
        println("=== ===")
        gl.check { bufferData(GL_ARRAY_BUFFER, sourceData, vertices.size, GL_STATIC_DRAW) }
        gl.check { bindBuffer(GL_ARRAY_BUFFER, null) }
        gl.check { bindVertexArray(null) }
    }

    private fun bufferOf(floats: FloatArray): Buffer = FloatBuffer(floats)

    internal fun prepareToRender(vertexAttr: Int, fn: () -> Unit) {
        gl.check { bindVertexArray(vao) } // <- required on lwjgl, not on webgl :-(
        gl.check { bindBuffer(GL_ARRAY_BUFFER, quadVertexBuffer) }
        gl.check { vertexAttribPointer(vertexAttr, 2, GL_FLOAT, false, 0, 0) }
        gl.check { enableVertexAttribArray(vertexAttr) }

        fn()

        gl.check { disableVertexAttribArray(vertexAttr) }
        gl.check { bindBuffer(GL_ARRAY_BUFFER, null) }
        gl.check { bindVertexArray(null) }
    }

    internal fun renderRect(rectIndex: Int) {
        // Draw the triangles
        gl.check { drawArrays(GL_TRIANGLES, rectIndex * 6, 6) }
    }

    fun release() {
        if (!released) {
            gl.runInContext {
                gl.check { deleteBuffer(quadVertexBuffer) }
                gl.check { deleteVertexArray(vao) }
            }
            released = true
        }
    }

    fun finalize() {
        release()
    }

    data class Rect(val top: Float, val left: Float, val bottom: Float, val right: Float) {
        fun scaleToUv(width: Int, height: Int) = Rect(
            -(top / height * 2 - 1),
            left / width * 2 - 1,
            -(bottom / height * 2 - 1),
            right / width * 2 - 1
        )
    }

    companion object {
        private val logger = Logger<Quad>()
        val quadRect2x2 = Rect(1f, -1f, -1f, 1f)
    }
}