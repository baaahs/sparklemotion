package baaahs.glsl

import com.danielgergely.kgl.*

class Quad(private val gl: Kgl, program: Program) {
    private val vertices = arrayOf(
        // First triangle:
        1.0f, 1.0f,
        -1.0f, 1.0f,
        -1.0f, -1.0f,
        // Second triangle:
        -1.0f, -1.0f,
        1.0f, -1.0f,
        1.0f, 1.0f
    ).toFloatArray()

    private var vao: VertexArrayObject = gl { gl.createVertexArray() }
    private var quadVertexBuffer: GlBuffer = gl { gl.createBuffers(1)[0] }
    private val vertexAttr = gl { program.getVertexAttribLocation() }

    init {
        gl { gl.bindVertexArray(vao) }
        gl { gl.bindBuffer(GL_ARRAY_BUFFER, quadVertexBuffer) }
        gl { gl.bufferData(GL_ARRAY_BUFFER, bufferOf(vertices), vertices.size, GL_STATIC_DRAW) }

        gl { gl.vertexAttribPointer(vertexAttr, 2, GL_FLOAT, false, 0, 0) }
        gl { gl.enableVertexAttribArray(vertexAttr) }

        gl { gl.bindBuffer(GL_ARRAY_BUFFER, null) }

        gl { gl.bindVertexArray(null) }
    }

    private fun bufferOf(floats: FloatArray): Buffer = FloatBuffer(floats)

    internal fun render() {
        gl { gl.bindVertexArray(vao) }
        gl { gl.enableVertexAttribArray(vertexAttr) }

        // Draw the triangles
        gl { gl.drawArrays(GL_TRIANGLES, 0, 6) }

        gl { gl.disableVertexAttribArray(vertexAttr) }
        gl { gl.bindVertexArray(null) }
    }

    fun release() {
        gl { gl.deleteBuffer(quadVertexBuffer) }
        gl { gl.deleteVertexArray(vao) }
    }

    fun <T> gl(fn: () -> T): T {
        val result = fn.invoke()
        gl.checkForGlError()
        return result
    }
}