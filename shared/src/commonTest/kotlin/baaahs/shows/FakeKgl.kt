package baaahs.shows

import baaahs.geom.*
import baaahs.getBang
import baaahs.gl.GlContext
import baaahs.gl.glsl.CompiledShader
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.RenderTarget
import baaahs.glsl.GlslUniform
import baaahs.glsl.TextureUniform
import com.danielgergely.kgl.*

class FakeGlContext(
    internal val fakeKgl: FakeKgl = FakeKgl()
) : GlContext("FakeGlContext", fakeKgl, "1234") {
    val programs get() = fakeKgl.programs.let { it.subList(1, it.size) }
    val textures get() = fakeKgl.textures.let { it.subList(1, it.size) }

    override fun <T> runInContext(fn: () -> T): T {
        ++fakeKgl.nestLevel
        try { return fn() } finally { fakeKgl.nestLevel-- }
    }

    override suspend fun <T> asyncRunInContext(fn: suspend () -> T): T {
        ++fakeKgl.nestLevel
        try { return fn() } finally { fakeKgl.nestLevel-- }
    }

    override fun getGlInt(parameter: Int): Int =
        mapOf(
            GL_MAX_TEXTURE_SIZE to 1024 * 16,
            GL_MAX_TEXTURE_IMAGE_UNITS to 8
        )[parameter] ?: error("unknown parameter $parameter")

    fun getTextureConfig(textureUnit: Int, target: Int): FakeKgl.TextureConfig {
        return fakeKgl.getTextureConfig(textureUnit, target)
            ?: error("no texture bound on unit $textureUnit")
    }

    fun findProgram(id: Program) = fakeKgl.programs[defake(id)]
}

// Until mockk works on JS:
class FakeKgl : Kgl {
    var nestLevel = 0

    internal val shaders = arrayListOf(FakeShader(0)) // 1-based
    internal val programs = arrayListOf(FakeProgram()) // 1-based
    private var activeProgram: FakeProgram? = null

    internal val textures = arrayListOf(TextureConfig()) // 1-based
    private var activeTextureUnit: FakeTextureUnit? = null
    private var textureUnits: MutableMap<Int, FakeTextureUnit> = mutableMapOf()

    private fun checkContext() {
        if (nestLevel == 0) error("ran GL command outside of context!")
    }

    inner class FakeShader(val type: Int) {
        var src: String? = null
    }

    inner class FakeTextureUnit(val unitNumber: Int) {
        val targets: MutableMap<Int, Texture?> = mutableMapOf()
    }

    inner class FakeProgram {
        val shaders = mutableMapOf<Int, FakeShader>()
        val uniforms = arrayListOf<Any?>(null) // 1-based
        private val uniformIdsByName = mutableMapOf<String, Int>()
        val renders = mutableListOf<RenderState>()
        var deleted: Boolean = false

        fun getUniformLocation(name: String): UniformLocation {
            return fake(uniformIdsByName.getOrPut(name) {
                uniforms.add(null)
                uniforms.size - 1
            })
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> getUniform(name: String): T {
            return uniforms[uniformIdsByName.getBang(name, "uniform")] as T
        }

        fun uniformNames() = uniformIdsByName.keys.toSet()

        fun recordRender() {
            renders.add(
                RenderState(
                    uniformIdsByName.mapValues { (_, uniformId) -> uniforms[uniformId] },
                    HashMap(textureUnits),
                    textures.map { it.copy() }
                )
            )
        }
    }

    inner class RenderState(
        val uniforms: Map<String, Any?>,
        private val textureUnits: Map<Int, FakeTextureUnit>,
        private val textures: List<TextureConfig> // 1-based
    ) {
        fun findUniformTexture(uniformName: String): Texture {
            val textureUnitNumber = uniforms[uniformName] ?: error("No uniform $uniformName.")
            val textureUnit = textureUnits[textureUnitNumber] ?: error("No texture unit $textureUnitNumber.")
            return textureUnit.targets[GL_TEXTURE_2D] ?: error("No texture bound to unit $textureUnitNumber.")
        }

        fun findUniformTextureConfig(uniformName: String): TextureConfig =
            findUniformTexture(uniformName).let { textures[defake(it)] }
    }

    data class TextureConfig(
        var params: MutableMap<Int, Int> = mutableMapOf(),
        var level: Int? = null,
        var internalFormat: Int? = null,
        var width: Int? = null,
        var height: Int? = null,
        var border: Int? = null,
        var format: Int? = null,
        var type: Int? = null,
        var buffer: List<Any>? = null,
        var offset: Int? = null
    ) {
        var isDeleted: Boolean = false

        fun delete() {
            if (isDeleted) error("Already deleted.")
            isDeleted = true
        }
    }

    override fun activeTexture(texture: Int) {
        checkContext()
        activeTextureUnit = if (texture == 0) {
            null
        } else {
            val unitNumber = texture - GL_TEXTURE0
            textureUnits.getOrPut(unitNumber) { FakeTextureUnit(unitNumber) }
        }
    }

    override fun attachShader(programId: Program, shaderId: Shader) {
        checkContext()

        val fakeProgram = programs[defake(programId)]
        val fakeShader = shaders[defake(shaderId)]
        fakeProgram.shaders[fakeShader.type] = fakeShader
    }

    override fun bindAttribLocation(programId: Program, index: Int, name: String) { checkContext() }

    override fun bindBuffer(target: Int, bufferId: GlBuffer?) { checkContext() }

    override fun bindFramebuffer(target: Int, framebuffer: Framebuffer?) { checkContext() }

    override fun bindRenderbuffer(target: Int, renderbuffer: Renderbuffer?) { checkContext() }

    override fun bindTexture(target: Int, texture: Texture?) {
        checkContext()
        if (texture == null) {
            activeTextureUnit?.targets?.remove(target)
        } else {
            activeTextureUnit?.targets?.put(target, texture)
        }
    }

    override fun bindVertexArray(vertexArrayObject: VertexArrayObject?) { checkContext() }

    override fun blendFunc(sFactor: Int, dFactor: Int) { checkContext() }

    override fun bufferData(target: Int, sourceData: Buffer, size: Int, usage: Int) { checkContext() }

    override fun checkFramebufferStatus(target: Int): Int = 1

    override fun clear(mask: Int) { checkContext() }

    override fun clearColor(r: Float, g: Float, b: Float, a: Float) { checkContext() }

    override fun compileShader(shaderId: Shader) { checkContext() }

    override fun createBuffer(): GlBuffer = fake(1)

    override fun createBuffers(count: Int): Array<GlBuffer> = Array(count) { createBuffer() }

    override fun createFramebuffer(): Framebuffer = fake(1)

    override fun createProgram(): Program? {
        checkContext()
        programs.add(FakeProgram())
        return fake(programs.size - 1)
    }

    override fun createRenderbuffer(): Renderbuffer {
        checkContext()
        return fake(1)
    }

    override fun createShader(type: Int): Shader? {
        checkContext()
        shaders.add(FakeShader(type))
        return fake(shaders.size - 1)
    }

    override fun createTexture(): Texture {
        checkContext()
        textures.add(TextureConfig())
        return fake(textures.size - 1)
    }

    override fun createTextures(n: Int): Array<Texture> = Array(n) { createTexture() }

    override fun createVertexArray(): VertexArrayObject {
        checkContext()
        return fake(1)
    }

    override fun cullFace(mode: Int) { checkContext() }

    override fun deleteBuffer(buffer: GlBuffer) { checkContext() }

    override fun deleteFramebuffer(framebuffer: Framebuffer) { checkContext() }

    override fun deleteRenderbuffer(renderbuffer: Renderbuffer) { checkContext() }

    override fun deleteShader(shaderId: Shader) { checkContext() }

    override fun deleteTexture(texture: Texture) {
        checkContext()
        val textureConfig = textures[defake(texture)]
        textureConfig.delete()
    }

    override fun deleteVertexArray(vertexArrayObject: VertexArrayObject) { checkContext() }

    override fun disable(cap: Int) { checkContext() }

    override fun disableVertexAttribArray(location: Int) { checkContext() }

    override fun drawArrays(mode: Int, first: Int, count: Int) {
        checkContext()
        activeProgram?.recordRender()
    }

    override fun drawElements(mode: Int, count: Int, type: Int) {
        checkContext()
        activeProgram?.recordRender()
    }

    override fun enable(cap: Int) { checkContext() }

    override fun enableVertexAttribArray(location: Int) { checkContext() }

    override fun finish() { checkContext() }

    override fun framebufferRenderbuffer(
        target: Int,
        attachment: Int,
        renderbuffertarget: Int,
        renderbuffer: Renderbuffer
    ) {
        checkContext()
    }

    override fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Texture, level: Int) { checkContext() }

    override fun generateMipmap(target: Int) { checkContext() }

    override fun getAttribLocation(programId: Program, name: String): Int {
        checkContext()
        return 1
    }

    override fun getError(): Int {
        checkContext()
        return 0
    }

    override fun getProgramInfoLog(program: Program): String? {
        checkContext()
        return null
    }

    override fun getProgramParameter(program: Program, pname: Int): Int {
        checkContext()
        return GL_TRUE
    }

    override fun getShaderInfoLog(shaderId: Shader): String? {
        checkContext()
        return null
    }

    override fun getShaderParameter(shader: Shader, pname: Int): Int {
        checkContext()
        return GL_TRUE
    }

    override fun getUniformLocation(programId: Program, name: String): UniformLocation? {
        checkContext()
        return programs[defake(programId)].getUniformLocation(name)
    }

    override fun isFramebuffer(framebuffer: Framebuffer): Boolean {
        checkContext()
        return true
    }

    override fun isRenderbuffer(renderbuffer: Renderbuffer): Boolean {
        checkContext()
        return true
    }

    override fun linkProgram(programId: Program) { checkContext() }

    override fun readPixels(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        format: Int,
        type: Int,
        buffer: Buffer
    ) {
        checkContext()
    }

    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) { checkContext() }

    override fun shaderSource(shaderId: Shader, source: String) {
        checkContext()
        val fakeShader = shaders[defake(shaderId)]
        fakeShader.src = source
    }

    override fun texImage2D(target: Int, level: Int, internalFormat: Int, border: Int, resource: TextureResource) { checkContext() }

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
        checkContext()
        val texture = activeTextureUnit?.targets?.get(target) ?: error("No texture bound to unit $target.")
        textures[defake(texture)]
            .apply {
                this.level = level
                this.internalFormat = internalFormat
                this.width = width
                this.height = height
                this.border = border
                this.format = format
                this.type = type
                this.buffer = buffer.contents()
                this.offset = null
            }
    }

    override fun texParameteri(target: Int, pname: Int, value: Int) {
        checkContext()
        val texture = activeTextureUnit?.targets?.get(target) ?: error("No texture bound to unit $target.")
        textures[defake(texture)].params[pname] = value
    }

    fun UniformLocation.set(value: Any) {
        (activeProgram ?: error("No active program")).uniforms[defake(this)] = value
    }

    override fun uniform1f(location: UniformLocation, f: Float) {
        checkContext()
        location.set(f)
    }

    override fun uniform1fv(location: UniformLocation, value: FloatArray) {
        checkContext()
        location.set(value)
    }

    override fun uniform1i(location: UniformLocation, i: Int) {
        checkContext()
        location.set(i)
    }

    override fun uniform1iv(location: UniformLocation, value: IntArray) {
        checkContext()
        location.set(value)
    }

    override fun uniform2f(location: UniformLocation, x: Float, y: Float) {
        checkContext()
        location.set(listOf(x, y))
    }

    override fun uniform2fv(location: UniformLocation, value: FloatArray) {
        checkContext()
        location.set(value)
    }

    override fun uniform2i(location: UniformLocation, x: Int, y: Int) {
        checkContext()
        location.set(listOf(x, y))
    }

    override fun uniform2iv(location: UniformLocation, value: IntArray) {
        checkContext()
        location.set(value)
    }

    override fun uniform3f(location: UniformLocation, x: Float, y: Float, z: Float) {
        checkContext()
        location.set(listOf(x, y, z))
    }

    override fun uniform3fv(location: UniformLocation, value: FloatArray) {
        checkContext()
        location.set(value)
    }

    override fun uniform3i(location: UniformLocation, x: Int, y: Int, z: Int) {
        checkContext()
        location.set(listOf(x, y, z))
    }

    override fun uniform3iv(location: UniformLocation, value: IntArray) {
        checkContext()
        location.set(value)
    }

    override fun uniform4f(location: UniformLocation, x: Float, y: Float, z: Float, w: Float) {
        checkContext()
        location.set(listOf(x, y, z, w))
    }

    override fun uniform4fv(location: UniformLocation, value: FloatArray) {
        checkContext()
        location.set(value)
    }

    override fun uniform4i(location: UniformLocation, x: Int, y: Int, z: Int, w: Int) {
        checkContext()
        location.set(listOf(x, y, z, w))
    }

    override fun uniform4iv(location: UniformLocation, value: IntArray) {
        checkContext()
        location.set(value)
    }

    override fun uniformMatrix3fv(location: UniformLocation, transpose: Boolean, value: FloatArray) {
        checkContext()
        location.set(value.toList())
    }

    override fun uniformMatrix4fv(location: UniformLocation, transpose: Boolean, value: FloatArray) {
        checkContext()
        location.set(value.toList())
    }

    override fun useProgram(programId: Program) {
        checkContext()
        activeProgram = programs[defake(programId)]
    }

    override fun deleteProgram(programId: Program) {
        checkContext()
        programs[defake(programId)].deleted = true
    }

    override fun detachShader(programId: Program, shaderId: Shader) {
        checkContext()
        programs[defake(programId)].shaders.remove(defake(shaderId))
    }

    override fun vertexAttribPointer(
        location: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        offset: Int
    ) {
        checkContext()
    }

    override fun viewport(x: Int, y: Int, width: Int, height: Int) { checkContext() }

    fun getTextureConfig(textureUnit: Int, target: Int): TextureConfig? {
        val texture = textureUnits[textureUnit]?.targets?.get(target) ?: return null
        return textures[defake(texture)]
    }

    companion object {
        private fun <T> dump(callback: (Int) -> T): List<T> =
            arrayListOf<T>()
                .also { list ->
                    try {
                        var i = 0
                        while (true) list.add(callback(i++))
                    } catch (e: Exception) {
                        // Cool, out of items.
                    }
                }

        fun ByteBuffer.contents() = dump { i -> this[i] }
        fun FloatBuffer.contents() = dump { i -> this[i] }
        fun Buffer.contents() =
            when(this) {
                is ByteBuffer -> this.contents()
                is FloatBuffer -> this.contents()
                else -> error("unknown type")
            }
    }
}

class FakeUniform(override val name: String = "fake") : GlslUniform {
    var value: Any? = null

    override fun set(x: Int) { value = x }
    override fun set(x: Int, y: Int) { value = listOf(x, y) }
    override fun set(x: Int, y: Int, z: Int) { value = listOf(x, y, z) }
    override fun set(x: Int, y: Int, z: Int, w: Int) { value = listOf(x, y, z, w) }
    override fun set(x: Float) { value = x }
    override fun set(x: Float, y: Float) { value = Vector2F(x, y) }
    override fun set(x: Float, y: Float, z: Float) { value = Vector3F(x, y, z) }
    override fun set(x: Float, y: Float, z: Float, w: Float) { value = Vector4F(x, y, z, w) }
    override fun set(matrix: Matrix4F) { value = matrix }
    override fun set(vector2F: Vector2F) { value = vector2F }
    override fun set(vector3F: Vector3F) { value = vector3F }
    override fun set(vector4F: Vector4F) { value = vector4F }
    override fun set(eulerAngle: EulerAngle) { value }
}

open class StubGlslProgram : GlslProgram {
    override val title: String get() = TODO("not implemented")
    override val fragShader: CompiledShader get() = TODO("not implemented")
    override val vertexAttribLocation: Int get() = TODO("not implemented")
    override fun setResolution(x: Float, y: Float): Unit = TODO("not implemented")
    override fun aboutToRenderFrame(): Unit = TODO("not implemented")
    override fun setPixDimens(width: Int, height: Int) = TODO("not implemented")
    override fun aboutToRenderFixture(renderTarget: RenderTarget): Unit = TODO("not implemented")
    override fun getUniform(name: String): GlslUniform? = TODO("not implemented")
    override fun getTextureUniform(name: String): TextureUniform? = TODO("not implemented")
    override fun <T> withProgram(fn: Kgl.() -> T): T = TODO("not implemented")
    override fun validate():Unit = TODO("not implemented")
    override fun use(): Unit = TODO("not implemented")
    override fun release(): Unit = TODO("not implemented")
}

class FakeGlslProgram(
    val uniforms: MutableMap<String, FakeUniform?> = mutableMapOf()
) : StubGlslProgram() {
    override fun getUniform(name: String): GlslUniform? {
        return uniforms.getOrPut(name) { FakeUniform() }
    }
}

private fun <T : Any> fake(i: Int): T {
    @Suppress("UNCHECKED_CAST")
    return i as T
}

private fun <T : Any> defake(i: T): Int {
    return i as Int
}