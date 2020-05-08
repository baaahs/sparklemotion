package baaahs.shows

import baaahs.glsl.GlslContext
import com.danielgergely.kgl.*

class FakeGlslContext(private val kgl: FakeKgl = FakeKgl()) : GlslContext(kgl, "1234") {
    val programs: List<FakeKgl.FakeProgram>
        get() = kgl.programs

    override fun <T> runInContext(fn: () -> T): T = fn()

    fun getTextureConfig(textureUnit: Int): FakeKgl.TextureConfig {
        return kgl.getTextureConfig(textureUnit)
            ?: error("no texture bound on unit $textureUnit")
    }
}

// Until mockk works on JS:
class FakeKgl : Kgl {
    internal val programs = arrayListOf(FakeProgram()) // 1-based
    private var activeProgram: FakeProgram? = null

    private val uniforms = arrayListOf<Any?>(null) // 1-based

    private val textures = arrayListOf(TextureConfig()) // 1-based
    private var activeTextureUnit: Int? = null
    private var textureUnits: MutableMap<Int, TextureConfig> = mutableMapOf()
    private var targets: MutableMap<Int, TextureConfig> = mutableMapOf()

    inner class FakeProgram {
        private val uniformIdsByName = mutableMapOf<String, Int>()

        fun getUniformLocation(name: String): UniformLocation? {
            return fake(uniformIdsByName.getOrPut(name) {
                uniforms.add(null)
                uniforms.size - 1
            })
        }

        fun getUniform(name: String): Any? {
            return uniforms[uniformIdsByName[name] ?: error("unknown uniform $name")]
        }
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
    )

    override fun activeTexture(texture: Int) {
        activeTextureUnit = texture - GL_TEXTURE0
    }

    override fun attachShader(programId: Program, shaderId: Shader) {}

    override fun bindAttribLocation(programId: Program, index: Int, name: String) {}

    override fun bindBuffer(target: Int, bufferId: GlBuffer?) {}

    override fun bindFramebuffer(target: Int, framebuffer: Framebuffer?) {}

    override fun bindRenderbuffer(target: Int, renderbuffer: Renderbuffer?) {}

    override fun bindTexture(target: Int, texture: Texture?) {
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

    override fun bindVertexArray(vertexArrayObject: VertexArrayObject?) {}

    override fun blendFunc(sFactor: Int, dFactor: Int) {}

    override fun bufferData(target: Int, sourceData: Buffer, size: Int, usage: Int, offset: Int) {}

    override fun checkFramebufferStatus(target: Int): Int = 1

    override fun clear(mask: Int) {}

    override fun clearColor(r: Float, g: Float, b: Float, a: Float) {}

    override fun compileShader(shaderId: Shader) {}

    override fun createBuffer(): GlBuffer = fake(1)

    override fun createBuffers(count: Int): Array<GlBuffer> = Array(count) { createBuffer() }

    override fun createFramebuffer(): Framebuffer = fake(1)

    override fun createProgram(): Program? {
        programs.add(FakeProgram())
        return fake(programs.size - 1)
    }

    override fun createRenderbuffer(): Renderbuffer = fake(1)

    override fun createShader(type: Int): Shader? = fake(1)

    override fun createTexture(): Texture {
        textures.add(TextureConfig())
        return fake(textures.size - 1)
    }

    override fun createTextures(n: Int): Array<Texture> = Array(n) { createTexture() }

    override fun createVertexArray(): VertexArrayObject = fake(1)

    override fun cullFace(mode: Int) {}

    override fun deleteBuffer(buffer: GlBuffer) {}

    override fun deleteFramebuffer(framebuffer: Framebuffer) {}

    override fun deleteRenderbuffer(renderbuffer: Renderbuffer) {}

    override fun deleteShader(shaderId: Shader) {}

    override fun deleteTexture(texture: Texture) {}

    override fun deleteVertexArray(vertexArrayObject: VertexArrayObject) {}

    override fun disable(cap: Int) {}

    override fun disableVertexAttribArray(location: Int) {}

    override fun drawArrays(mode: Int, first: Int, count: Int) {}

    override fun enable(cap: Int) {}

    override fun enableVertexAttribArray(location: Int) {}

    override fun finish() {}

    override fun framebufferRenderbuffer(
        target: Int,
        attachment: Int,
        renderbuffertarget: Int,
        renderbuffer: Renderbuffer
    ) {}

    override fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Texture, level: Int) {}

    override fun generateMipmap(target: Int) {}

    override fun getAttribLocation(programId: Program, name: String): Int = 1

    override fun getError(): Int = 0

    override fun getProgramInfoLog(program: Program): String? = null

    override fun getProgramParameter(program: Program, pname: Int): Int =
        GL_TRUE

    override fun getShaderInfoLog(shaderId: Shader): String? = TODO()

    override fun getShaderParameter(shader: Shader, pname: Int): Int = 1

    override fun getUniformLocation(programId: Program, name: String): UniformLocation? {
        @Suppress("CAST_NEVER_SUCCEEDS")
        return programs[programId as Int].getUniformLocation(name)
    }

    override fun isFramebuffer(framebuffer: Framebuffer): Boolean = true

    override fun isRenderbuffer(renderbuffer: Renderbuffer): Boolean = true

    override fun linkProgram(programId: Program) {}

    override fun readPixels(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        format: Int,
        type: Int,
        buffer: Buffer,
        offset: Int
    ) {}

    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) {}

    override fun shaderSource(shaderId: Shader, source: String) {}

    override fun texImage2D(target: Int, level: Int, internalFormat: Int, border: Int, resource: TextureResource) {}

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
        targets[target]!!.params[pname] = value
    }

    fun UniformLocation.set(value: Any) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        uniforms[this as Int] = value
    }

    override fun uniform1f(location: UniformLocation, f: Float) {
        location.set(f)
    }

    override fun uniform1i(location: UniformLocation, i: Int) {
        location.set(i)
    }

    override fun uniform2f(location: UniformLocation, x: Float, y: Float) {
        location.set(listOf(x, y))
    }

    override fun uniform2i(location: UniformLocation, x: Int, y: Int) {
        location.set(listOf(x, y))
    }

    override fun uniform3f(location: UniformLocation, x: Float, y: Float, z: Float) {
        location.set(listOf(x, y, z))
    }

    override fun uniform3fv(location: UniformLocation, value: FloatArray) {
        location.set(value)
    }

    override fun uniform3i(location: UniformLocation, x: Int, y: Int, z: Int) {
        location.set(listOf(x, y, z))
    }

    override fun uniform4f(location: UniformLocation, x: Float, y: Float, z: Float, w: Float) {
        location.set(listOf(x, y, z, w))
    }

    override fun uniform4i(location: UniformLocation, x: Int, y: Int, z: Int, w: Int) {
        location.set(listOf(x, y, z, w))
    }

    override fun uniformMatrix3fv(location: UniformLocation, transpose: Boolean, value: FloatArray) {}

    override fun uniformMatrix4fv(location: UniformLocation, transpose: Boolean, value: FloatArray) {}

    override fun useProgram(programId: Program) {
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
    ) {}

    override fun viewport(x: Int, y: Int, width: Int, height: Int) {}

    private fun <T> fake(i: Int) : T {
        @Suppress("UNCHECKED_CAST")
        return i as T
    }

    fun getTextureConfig(textureUnit: Int): TextureConfig? {
        return textureUnits[textureUnit]
    }
}