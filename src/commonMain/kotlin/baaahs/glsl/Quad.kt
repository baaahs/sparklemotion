package baaahs.glsl

import com.danielgergely.kgl.*

class Quad(private val gl: GlslContext, rects: List<Rect>) {
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

    fun bind(vertexAttr: Int) {
        gl.check { bindVertexArray(vao) }
        gl.check { bindBuffer(GL_ARRAY_BUFFER, quadVertexBuffer) }
        gl.check { bufferData(GL_ARRAY_BUFFER, bufferOf(vertices), vertices.size, GL_STATIC_DRAW) }

        gl.check { vertexAttribPointer(vertexAttr, 2, GL_FLOAT, false, 0, 0) }
        gl.check { enableVertexAttribArray(vertexAttr) }

        gl.check { bindBuffer(GL_ARRAY_BUFFER, null) }

        gl.check { bindVertexArray(null) }
    }

    private fun bufferOf(floats: FloatArray): Buffer = FloatBuffer(floats)

    internal fun prepareToRender(vertexAttr: Int, fn: () -> Unit) {
        gl.check { bindVertexArray(vao) }
        gl.check { enableVertexAttribArray(vertexAttr) }

        fn()

        gl.check { disableVertexAttribArray(vertexAttr) }
        gl.check { bindVertexArray(null) }
    }

    internal fun renderRect(rectIndex: Int) {
        // Draw the triangles
        gl.check { drawArrays(GL_TRIANGLES, rectIndex * 6, 6) }
    }

    fun release() {
        gl.check { deleteBuffer(quadVertexBuffer) }
        gl.check { deleteVertexArray(vao) }
    }

    data class Rect(val top: Float, val left: Float, val bottom: Float, val right: Float)
}