package baaahs.shows

import baaahs.getBang
import baaahs.gl.GlContext
import com.danielgergely.kgl.*

class FakeGlContext(private val kgl: FakeKgl = FakeKgl()) : GlContext(kgl, "1234") {
    val programs get() = kgl.programs.let { it.subList(1, it.size) }
    val textures get() = kgl.textures.let { it.subList(1, it.size) }
    val allocatedTextureUnits get() = textureUnits

    override fun <T> runInContext(fn: () -> T): T {
        ++kgl.nestLevel
        try { return fn() } finally { kgl.nestLevel-- }
    }

    fun getTextureConfig(textureUnit: Int): FakeKgl.TextureConfig {
        return kgl.getTextureConfig(textureUnit)
            ?: error("no texture bound on unit $textureUnit")
    }

    fun findProgram(id: Program) = kgl.programs[id as Int]
}

// Until mockk works on JS:
class FakeKgl : Kgl {
    var nestLevel = 0

    internal val shaders = arrayListOf(FakeShader(0)) // 1-based
    internal val programs = arrayListOf(FakeProgram()) // 1-based
    private var activeProgram: FakeProgram? = null

    private val uniforms = arrayListOf<Any?>(null) // 1-based

    internal val textures = arrayListOf(TextureConfig()) // 1-based
    private var activeTextureUnit: Int? = null
    private var textureUnits: MutableMap<Int, TextureConfig> = mutableMapOf()
    private var targets: MutableMap<Int, TextureConfig> = mutableMapOf()

    private fun checkContext() {
        if (nestLevel == 0) error("ran GL command outside of context!")
    }

    inner class FakeShader(val type: Int) {
        var src: String? = null
    }

    inner class FakeProgram {
        val shaders = mutableMapOf<Int, FakeShader>()
        private val uniformIdsByName = mutableMapOf<String, Int>()
        val renders = mutableListOf<RenderState>()

        fun getUniformLocation(name: String): UniformLocation? {
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
                RenderState(uniformIdsByName.mapValues { (_, uniformId) -> uniforms[uniformId] })
            )
        }
    }

    inner class RenderState(val uniforms: Map<String, Any?>) {
        val textureBuffers = textures.subList(1, textures.size)
            .map { it.buffer?.contents() ?: emptyList() }
    }

    class TextureConfig(
        var params: MutableMap<Int, Int> = mutableMapOf(),
        var level: Int? = null,
        var internalFormat: Int? = null,
        var width: Int? = null,
        var height: Int? = null,
        var border: Int? = null,
        var format: Int? = null,
        var type: Int? = null,
        var buffer: Buffer? = null,
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
        activeTextureUnit = texture - GL_TEXTURE0
    }

    override fun attachShader(programId: Program, shaderId: Shader) {
        checkContext()

        val fakeProgram = programs[programId as Int]
        val fakeShader = shaders[shaderId as Int]
        fakeProgram.shaders[fakeShader.type] = fakeShader
    }

    override fun bindAttribLocation(programId: Program, index: Int, name: String) { checkContext() }

    override fun bindBuffer(target: Int, bufferId: GlBuffer?) { checkContext() }

    override fun bindFramebuffer(target: Int, framebuffer: Framebuffer?) { checkContext() }

    override fun bindRenderbuffer(target: Int, renderbuffer: Renderbuffer?) { checkContext() }

    override fun bindTexture(target: Int, texture: Texture?) {
        checkContext()
        if (texture == null) {
            targets.remove(target)
            textureUnits.remove(activeTextureUnit)
        } else {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val boundTextureConfig = textures[texture as Int]
            targets[target] = boundTextureConfig
            textureUnits[activeTextureUnit!!] = boundTextureConfig
        }
    }

    override fun bindVertexArray(vertexArrayObject: VertexArrayObject?) { checkContext() }

    override fun blendFunc(sFactor: Int, dFactor: Int) { checkContext() }

    override fun bufferData(target: Int, sourceData: Buffer, size: Int, usage: Int, offset: Int) { checkContext() }

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
        @Suppress("CAST_NEVER_SUCCEEDS")
        return programs[programId as Int].getUniformLocation(name)
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
        buffer: Buffer,
        offset: Int
    ) {
        checkContext()
    }

    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) { checkContext() }

    override fun shaderSource(shaderId: Shader, source: String) {
        checkContext()
        val fakeShader = shaders[shaderId as Int]
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
        buffer: Buffer,
        offset: Int
    ) {
        checkContext()
        targets[target]!!.apply {
            this.level = level
            this.internalFormat = internalFormat
            this.width = width
            this.height = height
            this.border = border
            this.format = format
            this.type = type
            this.buffer = buffer
            this.offset = offset
        }
    }

    override fun texParameteri(target: Int, pname: Int, value: Int) {
        checkContext()
        targets[target]!!.params[pname] = value
    }

    fun UniformLocation.set(value: Any) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        uniforms[this as Int] = value
    }

    override fun uniform1f(location: UniformLocation, f: Float) {
        checkContext()
        location.set(f)
    }

    override fun uniform1i(location: UniformLocation, i: Int) {
        checkContext()
        location.set(i)
    }

    override fun uniform2f(location: UniformLocation, x: Float, y: Float) {
        checkContext()
        location.set(listOf(x, y))
    }

    override fun uniform2i(location: UniformLocation, x: Int, y: Int) {
        checkContext()
        location.set(listOf(x, y))
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

    override fun uniform4f(location: UniformLocation, x: Float, y: Float, z: Float, w: Float) {
        checkContext()
        location.set(listOf(x, y, z, w))
    }

    override fun uniform4i(location: UniformLocation, x: Int, y: Int, z: Int, w: Int) {
        checkContext()
        location.set(listOf(x, y, z, w))
    }

    override fun uniformMatrix3fv(location: UniformLocation, transpose: Boolean, value: FloatArray) { checkContext() }

    override fun uniformMatrix4fv(location: UniformLocation, transpose: Boolean, value: FloatArray) { checkContext() }

    override fun useProgram(programId: Program) {
        checkContext()
        @Suppress("CAST_NEVER_SUCCEEDS")
        activeProgram = programs[programId as Int]
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

    private fun <T> fake(i: Int): T {
        @Suppress("UNCHECKED_CAST")
        return i as T
    }

    private fun <T> defake(i: T): Int {
        @Suppress("UNCHECKED_CAST")
        return i as Int
    }

    fun getTextureConfig(textureUnit: Int): TextureConfig? {
        return textureUnits[textureUnit]
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