package baaahs.gl

import baaahs.util.Logger
import com.danielgergely.kgl.*

class KglTracer(private val kgl: Kgl) : Kgl {
    private val id = tracerCounter++
    private val logger = Logger<KglTracer>()
    private var nextObjId = 0

    override fun activeTexture(texture: Int) {
        log("activeTexture", texture)
        return kgl.activeTexture(texture)
    }

    override fun attachShader(programId: Program, shaderId: Shader) {
        log("attachShader", programId, shaderId)
        return kgl.attachShader(programId, shaderId)
    }

    override fun bindAttribLocation(programId: Program, index: Int, name: String) {
        log("bindAttribLocation", programId, index, name)
        return kgl.bindAttribLocation(programId, index, name)
    }

    override fun bindBuffer(target: Int, bufferId: GlBuffer?) {
        log("bindBuffer", target, bufferId)
        return kgl.bindBuffer(target, bufferId)
    }

    override fun bindFramebuffer(target: Int, framebuffer: Framebuffer?) {
        log("bindFramebuffer", target, framebuffer)
        return kgl.bindFramebuffer(target, framebuffer)
    }

    override fun bindRenderbuffer(target: Int, renderbuffer: Renderbuffer?) {
        log("bindRenderbuffer", target, renderbuffer)
        return kgl.bindRenderbuffer(target, renderbuffer)
    }

    override fun bindTexture(target: Int, texture: Texture?) {
        log("bindTexture", target, texture)
        return kgl.bindTexture(target, texture)
    }

    override fun bindVertexArray(vertexArrayObject: VertexArrayObject?) {
        log("bindVertexArray", vertexArrayObject)
        return kgl.bindVertexArray(vertexArrayObject)
    }

    override fun blendFunc(sFactor: Int, dFactor: Int) {
        log("blendFunc", sFactor, dFactor)
        return kgl.blendFunc(sFactor, dFactor)
    }

    override fun bufferData(target: Int, sourceData: Buffer, size: Int, usage: Int) {
        log("bufferData", target, sourceData, size, usage)
        return kgl.bufferData(target, sourceData, size, usage)
    }

    override fun checkFramebufferStatus(target: Int): Int {
        log("checkFramebufferStatus", target)
        return kgl.checkFramebufferStatus(target)
    }

    override fun clear(mask: Int) {
        log("clear", mask)
        return kgl.clear(mask)
    }

    override fun clearColor(r: Float, g: Float, b: Float, a: Float) {
        log("clearColor", r, g, b, a)
        return kgl.clearColor(r, g, b, a)
    }

    override fun compileShader(shaderId: Shader) {
        log("compileShader", shaderId)
        return kgl.compileShader(shaderId)
    }

    override fun createBuffer(): GlBuffer {
        return kgl.createBuffer().also {
            log("createBuffer") { it.withNote("buffer ${nextObjId++}") }
        }
    }

    override fun createBuffers(count: Int): Array<GlBuffer> {
        return kgl.createBuffers(count).also {
            log("createBuffers", count) { it }
        }
    }

    override fun createFramebuffer(): Framebuffer {
        return kgl.createFramebuffer().also {
            log("createFramebuffer") { it.withNote("Framebuffer ${nextObjId++}") }
        }
    }

    override fun createProgram(): Program? {
        return kgl.createProgram().also {
            log("createProgram") { it.withNote("Program ${nextObjId++}") }
        }
    }

    override fun createRenderbuffer(): Renderbuffer {
        return kgl.createRenderbuffer().also {
            log("createRenderbuffer") { it.withNote("Renderbuffer ${nextObjId++}") }
        }
    }

    override fun createShader(type: Int): Shader? {
        return kgl.createShader(type).also {
            log("createShader", type) { it.withNote("Shader ${nextObjId++}") }
        }
    }

    override fun createTexture(): Texture {
        return kgl.createTexture().also {
            log("createTexture") { it.withNote("Texture ${nextObjId++}") }
        }
    }

    override fun createTextures(n: Int): Array<Texture> {
        return kgl.createTextures(n).also {
            log("createTextures", n) { it }
        }
    }

    override fun createVertexArray(): VertexArrayObject {
        return kgl.createVertexArray().also {
            log("createVertexArray") { it.withNote("VertexArray ${nextObjId++}") }
        }
    }

    override fun cullFace(mode: Int) {
        log("cullFace", mode)
        return kgl.cullFace(mode)
    }

    override fun deleteBuffer(buffer: GlBuffer) {
        log("deleteBuffer", buffer)
        return kgl.deleteBuffer(buffer)
    }

    override fun deleteFramebuffer(framebuffer: Framebuffer) {
        log("deleteFramebuffer", framebuffer)
        return kgl.deleteFramebuffer(framebuffer)
    }

    override fun deleteRenderbuffer(renderbuffer: Renderbuffer) {
        log("deleteRenderbuffer", renderbuffer)
        return kgl.deleteRenderbuffer(renderbuffer)
    }

    override fun deleteShader(shaderId: Shader) {
        log("deleteShader", shaderId)
        return kgl.deleteShader(shaderId)
    }

    override fun deleteTexture(texture: Texture) {
        log("deleteTexture", texture)
        return kgl.deleteTexture(texture)
    }

    override fun deleteVertexArray(vertexArrayObject: VertexArrayObject) {
        log("deleteVertexArray", vertexArrayObject)
        return kgl.deleteVertexArray(vertexArrayObject)
    }

    override fun disable(cap: Int) {
        log("disable", cap)
        return kgl.disable(cap)
    }

    override fun disableVertexAttribArray(location: Int) {
        log("disableVertexAttribArray", location)
        return kgl.disableVertexAttribArray(location)
    }

    override fun drawArrays(mode: Int, first: Int, count: Int) {
        log("drawArrays", mode, first, count)
        return kgl.drawArrays(mode, first, count)
    }

    override fun enable(cap: Int) {
        log("enable", cap)
        return kgl.enable(cap)
    }

    override fun enableVertexAttribArray(location: Int) {
        log("enableVertexAttribArray", location)
        return kgl.enableVertexAttribArray(location)
    }

    override fun finish() {
        log("finish")
        return kgl.finish()
    }

    override fun framebufferRenderbuffer(
        target: Int,
        attachment: Int,
        renderbuffertarget: Int,
        renderbuffer: Renderbuffer
    ) {
        log("framebufferRenderbuffer", target, attachment, renderbuffertarget, renderbuffer)
        return kgl.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer)
    }

    override fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Texture, level: Int) {
        log("framebufferTexture2D", target, attachment, textarget, texture, level)
        return kgl.framebufferTexture2D(target, attachment, textarget, texture, level)
    }

    override fun generateMipmap(target: Int) {
        log("generateMipmap", target)
        return kgl.generateMipmap(target)
    }

    override fun getAttribLocation(programId: Program, name: String): Int {
        return kgl.getAttribLocation(programId, name).also {
            log("getAttribLocation", programId, name) {
                it.withNote("AttribLocation $name on ${stringify(programId)}")
            }
        }
    }

    override fun getError(): Int {
        val error = kgl.getError()
        if (error != GL_NO_ERROR) log("getError() => $error")
        return error
    }

    override fun getProgramInfoLog(program: Program): String? {
        log("getProgramInfoLog", program)
        return kgl.getProgramInfoLog(program)
    }

    override fun getProgramParameter(program: Program, pname: Int): Int {
        log("getProgramParameter", program, pname)
        return kgl.getProgramParameter(program, pname)
    }

    override fun getShaderInfoLog(shaderId: Shader): String? {
        log("getShaderInfoLog", shaderId)
        return kgl.getShaderInfoLog(shaderId)
    }

    override fun getShaderParameter(shader: Shader, pname: Int): Int {
        log("getShaderParameter", shader, pname)
        return kgl.getShaderParameter(shader, pname)
    }

    override fun getUniformLocation(programId: Program, name: String): UniformLocation? {
        return kgl.getUniformLocation(programId, name).also {
            log("getUniformLocation", programId, name) {
                it.withNote("Uniform $name on ${stringify(programId)}")
            }
        }
    }

    override fun isFramebuffer(framebuffer: Framebuffer): Boolean {
        log("isFramebuffer", framebuffer)
        return kgl.isFramebuffer(framebuffer)
    }

    override fun isRenderbuffer(renderbuffer: Renderbuffer): Boolean {
        log("isRenderbuffer", renderbuffer)
        return kgl.isRenderbuffer(renderbuffer)
    }

    override fun linkProgram(programId: Program) {
        log("linkProgram", programId)
        return kgl.linkProgram(programId)
    }

    override fun readPixels(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        format: Int,
        type: Int,
        buffer: Buffer
    ) {
        log("readPixels", x, y, width, height, format, type, buffer)
        return kgl.readPixels(x, y, width, height, format, type, buffer)
    }

    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) {
        log("renderbufferStorage", target, internalformat, width, height)
        return kgl.renderbufferStorage(target, internalformat, width, height)
    }

    override fun shaderSource(shaderId: Shader, source: String) {
        log("shaderSource", shaderId, source)
        return kgl.shaderSource(shaderId, source)
    }

    override fun texImage2D(target: Int, level: Int, internalFormat: Int, border: Int, resource: TextureResource) {
        log("texImage2D", target, level, internalFormat, border, resource)
        return kgl.texImage2D(target, level, internalFormat, border, resource)
    }

    override fun texImage2D(
        target: Int,
        level: Int,
        internalFormat: Int,
        width: Int,
        height: Int,
        border: Int,
        format: Int,
        type: Int,
        buffer: Buffer
    ) {
        log("texImage2D", target, level, internalFormat, width, height, border, format, type, buffer)
        return kgl.texImage2D(target, level, internalFormat, width, height, border, format, type, buffer)
    }

    override fun texParameteri(target: Int, pname: Int, value: Int) {
        log("texParameteri", target, pname, value)
        return kgl.texParameteri(target, pname, value)
    }

    override fun uniform1f(location: UniformLocation, f: Float) {
        log("uniform1f", location, f)
        return kgl.uniform1f(location, f)
    }

    override fun uniform1fv(location: UniformLocation, value: FloatArray) {
        log("uniform1fv", location, value)
        return kgl.uniform1fv(location, value)
    }

    override fun uniform1i(location: UniformLocation, i: Int) {
        log("uniform1i", location, i)
        return kgl.uniform1i(location, i)
    }

    override fun uniform1iv(location: UniformLocation, value: IntArray) {
        log("uniform1iv", location, value)
        return kgl.uniform1iv(location, value)
    }

    override fun uniform2f(location: UniformLocation, x: Float, y: Float) {
        log("uniform2f", location, x, y)
        return kgl.uniform2f(location, x, y)
    }

    override fun uniform2fv(location: UniformLocation, value: FloatArray) {
        log("uniform2fv", location, value)
        return kgl.uniform2fv(location, value)
    }

    override fun uniform2i(location: UniformLocation, x: Int, y: Int) {
        log("uniform2i", location, x, y)
        return kgl.uniform2i(location, x, y)
    }

    override fun uniform2iv(location: UniformLocation, value: IntArray) {
        log("uniform2iv", location, value)
        return kgl.uniform2iv(location, value)
    }

    override fun uniform3f(location: UniformLocation, x: Float, y: Float, z: Float) {
        log("uniform3f", location, x, y, z)
        return kgl.uniform3f(location, x, y, z)
    }

    override fun uniform3fv(location: UniformLocation, value: FloatArray) {
        log("uniform3fv", location, value)
        return kgl.uniform3fv(location, value)
    }

    override fun uniform3i(location: UniformLocation, x: Int, y: Int, z: Int) {
        log("uniform3i", location, x, y, z)
        return kgl.uniform3i(location, x, y, z)
    }

    override fun uniform3iv(location: UniformLocation, value: IntArray) {
        log("uniform3iv", location, value)
        return kgl.uniform3iv(location, value)
    }

    override fun uniform4f(location: UniformLocation, x: Float, y: Float, z: Float, w: Float) {
        log("uniform4f", location, x, y, z, w)
        return kgl.uniform4f(location, x, y, z, w)
    }

    override fun uniform4fv(location: UniformLocation, value: FloatArray) {
        log("uniform4fv", location, value)
        return kgl.uniform4fv(location, value)
    }

    override fun uniform4i(location: UniformLocation, x: Int, y: Int, z: Int, w: Int) {
        log("uniform4i", location, x, y, z, w)
        return kgl.uniform4i(location, x, y, z, w)
    }

    override fun uniform4iv(location: UniformLocation, value: IntArray) {
        log("uniform4iv", location, value)
        return kgl.uniform4iv(location, value)
    }

    override fun uniformMatrix3fv(location: UniformLocation, transpose: Boolean, value: FloatArray) {
        log("uniformMatrix3fv", location, transpose, value)
        return kgl.uniformMatrix3fv(location, transpose, value)
    }

    override fun uniformMatrix4fv(location: UniformLocation, transpose: Boolean, value: FloatArray) {
        log("uniformMatrix4fv", location, transpose, value)
        return kgl.uniformMatrix4fv(location, transpose, value)
    }

    override fun useProgram(programId: Program) {
        log("useProgram", programId)
        return kgl.useProgram(programId)
    }

    override fun deleteProgram(programId: Program) {
        log("deleteProgram", programId)
        return kgl.deleteProgram(programId)
    }

    override fun detachShader(programId: Program, shaderId: Shader) {
        log("detachShader", programId, shaderId)
        return kgl.detachShader(programId, shaderId)
    }

    override fun vertexAttribPointer(
        location: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        offset: Int
    ) {
        log("vertexAttribPointer", location, size, type, normalized, stride, offset)
        return kgl.vertexAttribPointer(location, size, type, normalized, stride, offset)
    }

    override fun viewport(x: Int, y: Int, width: Int, height: Int) {
        log("viewport", x, y, width, height)
        return kgl.viewport(x, y, width, height)
    }

    private fun log(name: String, vararg args: Any?, fn: (() -> Any?)? = null) {
        logger.info {
            val argsStr = args.joinToString(", ") { stringify(it) }
            "ctx$id: $id $name($argsStr)${fn?.let { " => ${stringify(fn())}" } ?: ""}"
        }
    }

    private fun stringify(it: Any?) =
        if (it is IntArray) {
            "[${it.joinToString(",")}]"
        } else if (it is FloatArray) {
            "[${it.joinToString(",")}]"
        } else if (it is Array<*>) {
            "[${it.joinToString(",")}]"
        } else it.toStringMaybeWithNote()

    companion object {
        private var tracerCounter = 0
    }
}

expect fun <T: Any?> T.withNote(note: String): T
expect fun <T: Any?> T.toStringMaybeWithNote(): String