package baaahs.gl.glsl

import baaahs.geom.*
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.render.RenderTarget
import baaahs.glsl.Uniform
import baaahs.show.DataSource
import baaahs.show.UpdateMode
import baaahs.util.Logger
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.UniformLocation

interface GlslProgram {
    val title: String
    val fragShader: CompiledShader
    val vertexAttribLocation: Int

    fun setResolution(x: Float, y: Float)
    fun setPixDimens(width: Int, height: Int)
    fun aboutToRenderFrame()
    fun aboutToRenderFixture(renderTarget: RenderTarget)

    fun getUniformInt(name: String): UniformInt
    fun getUniformIvec2(name: String): UniformIvec2
    fun getUniformIvec3(name: String): UniformIvec3
    fun getUniformIvec4(name: String): UniformIvec4
    fun getUniformFloat(name: String): UniformFloat
    fun getUniformVec2(name: String): UniformVec2
    fun getUniformVec3(name: String): UniformVec3
    fun getUniformVec4(name: String): UniformVec4
    fun getUniformMatrix(name: String): UniformMatrix
    fun getUniformTextureUnit(name: String): UniformTextureUnit

    fun <T> withProgram(fn: Kgl.() -> T): T
    fun use()
    fun release()

    interface ResolutionListener {
        fun onResolution(x: Float, y: Float)
    }

    fun Uniform<Vector2F>.set(x: Float, y: Float) = set(Vector2F(x, y))
    fun Uniform<Vector3F>.set(x: Float, y: Float, z: Float) = set(Vector3F(x, y, z))
    fun Uniform<Vector4F>.set(x: Float, y: Float, z: Float, w: Float) = set(Vector4F(x, y, z, w))

    interface UniformInt : Uniform<Int>
    interface UniformIvec2 : Uniform<Ivec2>
    interface UniformIvec3 : Uniform<Ivec3>
    interface UniformIvec4 : Uniform<Ivec4>
    interface UniformFloat : Uniform<Float>
    interface UniformVec2 : Uniform<Vector2F> {
        fun set(x: Float, y: Float)
    }
    interface UniformVec3 : Uniform<Vector3F> {
        fun set(x: Float, y: Float, z: Float)
    }
    interface UniformVec4 : Uniform<Vector4F> {
        fun set(x: Float, y: Float, z: Float, w: Float)
    }
    interface UniformMatrix : Uniform<Matrix4>
    interface UniformTextureUnit : Uniform<GlContext.TextureUnit>

    fun getUniformLocation(varName: String): UniformLocation?

    companion object {
        internal val logger = Logger<GlslProgram>()
    }
}

class GlslProgramImpl(
    private val gl: GlContext,
    private val linkedPatch: LinkedPatch,
    engineFeedResolver: EngineFeedResolver
): GlslProgram {
    override val title: String get() = linkedPatch.rootNode.title

    private val vertexShader =
        gl.createVertexShader(
            "#version ${gl.glslVersion}\n${GlslProgramImpl.vertexShader}"
        )

    override val fragShader =
        gl.createFragmentShader(linkedPatch.toFullGlsl(gl.glslVersion))

    val id = gl.compile(vertexShader, fragShader)

    private val feeds = gl.runInContext {
        linkedPatch.dataSourceLinks.mapNotNull { (dataSource, id) ->
            val engineFeed = engineFeedResolver.openFeed(id, dataSource)

            if (engineFeed != null) {
                val programFeed = engineFeed.bind(this)
                if (programFeed.isValid) {
                    programFeed
                } else {
                    GlslProgram.logger.debug { "Invalid feed for $dataSource $id: $programFeed" }
                    programFeed.release()
                    null
                }
            } else {
                GlslProgram.logger.warn { "No feed bound for $dataSource $id." }
                null
            }
        }
    }

    private val vertexShader_resolution by lazy { getUniformVec2("vertexShader_resolution") }

    init {
        gl.runInContext {
            feeds.forEach { programFeed ->
                if (programFeed.updateMode == UpdateMode.ONCE)
                    programFeed.setOnProgram()
            }
        }
    }

    private val perFrameFeeds = feeds.mapNotNull { programFeed ->
        if (programFeed.updateMode == UpdateMode.PER_FRAME)
            programFeed
        else null
    }

    private val perFixtureFeeds = feeds.mapNotNull { programFeed ->
        if (programFeed.updateMode == UpdateMode.PER_FIXTURE)
            programFeed
        else null
    }

    override val vertexAttribLocation: Int = gl.runInContext {
        gl.check { getAttribLocation(id, "Vertex") }
    }

    private inline fun <reified T> feedsOf(): List<T> = feeds.filterIsInstance<T>()

    override fun setResolution(x: Float, y: Float) {
        feedsOf<GlslProgram.ResolutionListener>().forEach { it.onResolution(x, y) }
    }

    override fun setPixDimens(width: Int, height: Int) {
        vertexShader_resolution.set(width.toFloat(), height.toFloat())
    }

    override fun aboutToRenderFrame() {
        perFrameFeeds.forEach { it.setOnProgram() }
    }

    override fun aboutToRenderFixture(renderTarget: RenderTarget) {
        perFixtureFeeds.forEach {
            it.setOnProgram(renderTarget)
        }
    }

    override fun release() {
        feeds.forEach { it.release() }

        gl.runInContext {
            gl.useProgram(this)
//            TODO: gl.check { detachShader(vertexShader) }
            vertexShader.release()
//            TODO: gl.check { detachShader(fragShader) }
            fragShader.release()

        }
//        TODO gl.runInContext { gl.check { deleteProgram(id) } }
    }

    override fun getUniformLocation(varName: String) =
        gl.runInContext {
            withProgram {
                gl.check { getUniformLocation(id, varName) }
                    .also { GlslProgram.logger.debug { "No such uniform \"$varName\" in \"$title\"." } }
            }
        }

    override fun getUniformInt(name: String) = UniformInt(name)
    override fun getUniformIvec2(name: String) = UniformIvec2(name)
    override fun getUniformIvec3(name: String) = UniformIvec3(name)
    override fun getUniformIvec4(name: String) = UniformIvec4(name)
    override fun getUniformFloat(name: String) = UniformFloat(name)
    override fun getUniformVec2(name: String) = UniformVec2(name)
    override fun getUniformVec3(name: String) = UniformVec3(name)
    override fun getUniformVec4(name: String) = UniformVec4(name)
    override fun getUniformMatrix(name: String) = UniformMatrix(name)
    override fun getUniformTextureUnit(name: String) = UniformTextureUnit(name)

    override fun <T> withProgram(fn: Kgl.() -> T): T {
        gl.useProgram(this)
        return gl.check(fn)
    }

    override fun use() {
        gl.check { useProgram(id) }
    }

    companion object {
        /**language=glsl*/
        val vertexShader = """
            precision highp float;

            // xy = vertex position in device pixel coordinates.
            in vec2 Vertex;
            
            uniform vec2 vertexShader_resolution;

            void main()
            {
                // scale vertex attribute to [-1,1] range
                gl_Position = vec4(Vertex / vertexShader_resolution * 2. - 1., 0.0, 1.0);
            }
        """.trimIndent()
    }

    abstract inner class BaseUniform<T>(
        private val name: String
    ) : Uniform<T> {
        private val uniformLocation by lazy {
            withProgram {
                getUniformLocation(id, name)
                    .also { GlslProgram.logger.debug { "No such uniform \"$name\" in \"$title\"." } }
            }
        }

        override val exists: Boolean
            get() = uniformLocation != null

        protected fun <T> ifFound(fn: Kgl.(uniformLocation: UniformLocation) -> T) {
            uniformLocation?.let { uniformLocation ->
                gl.runInContext {
                    withProgram {
                        fn(uniformLocation)
                    }
                }
            }
        }
    }

    inner class UniformInt(name: String) : BaseUniform<Int>(name), GlslProgram.UniformInt {
        override fun set(value: Int) = ifFound { uniform1i(it, value) }
    }

    inner class UniformIvec2(name: String) : BaseUniform<Ivec2>(name), GlslProgram.UniformIvec2 {
        override fun set(value: Ivec2) = ifFound { uniform2i(it, value.x, value.y) }
    }

    inner class UniformIvec3(name: String) : BaseUniform<Ivec3>(name), GlslProgram.UniformIvec3 {
        override fun set(value: Ivec3) = ifFound { uniform3i(it, value.x, value.y, value.z) }
    }

    inner class UniformIvec4(name: String) : BaseUniform<Ivec4>(name), GlslProgram.UniformIvec4 {
        override fun set(value: Ivec4) = ifFound { uniform4i(it, value.x, value.y, value.z, value.w) }
    }

    inner class UniformFloat(name: String) : BaseUniform<Float>(name), GlslProgram.UniformFloat {
        override fun set(value: Float) = ifFound { uniform1f(it, value) }
    }

    inner class UniformVec2(name: String) : BaseUniform<Vector2F>(name), GlslProgram.UniformVec2 {
        override fun set(value: Vector2F) = ifFound { uniform2f(it, value.x, value.y) }
        override fun set(x: Float, y: Float) = ifFound { uniform2f(it, x, y) }
    }

    inner class UniformVec3(name: String) : BaseUniform<Vector3F>(name), GlslProgram.UniformVec3 {
        override fun set(value: Vector3F) = ifFound { uniform3f(it, value.x, value.y, value.z) }
        override fun set(x: Float, y: Float, z: Float) = ifFound { uniform3f(it, x, y, z) }
    }

    inner class UniformVec4(name: String) : BaseUniform<Vector4F>(name), GlslProgram.UniformVec4 {
        override fun set(value: Vector4F) = ifFound { uniform4f(it, value.x, value.y, value.z, value.w) }
        override fun set(x: Float, y: Float, z: Float, w: Float) = ifFound { uniform4f(it, x, y, z, w) }
    }

    inner class UniformMatrix(name: String) : BaseUniform<Matrix4>(name), GlslProgram.UniformMatrix {
        override fun set(value: Matrix4) = ifFound {
            uniformMatrix4fv(it, false, value.elements.map { it.toFloat() }.toFloatArray())
        }
    }

    inner class UniformTextureUnit(name: String) : BaseUniform<GlContext.TextureUnit>(name), GlslProgram.UniformTextureUnit {
        override fun set(value: GlContext.TextureUnit) = ifFound {
            uniform1i(it, value.unitNumber)
        }
    }
}

fun interface FeedResolver {
    fun openFeed(id: String, dataSource: DataSource): Feed?
}

fun interface EngineFeedResolver {
    fun openFeed(id: String, dataSource: DataSource): EngineFeed?
}
