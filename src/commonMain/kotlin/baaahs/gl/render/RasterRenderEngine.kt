package baaahs.gl.render

import baaahs.device.FixtureType
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.identity
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.util.Logger
import baaahs.visualizer.remote.RemoteVisualizers
import com.danielgergely.kgl.GL_COLOR_BUFFER_BIT
import com.danielgergely.kgl.GL_DEPTH_BUFFER_BIT

class DisplaysRenderEngine(
    override val fixtureType: FixtureType
) : FixtureRenderEngine {
    override fun addFixture(fixture: Fixture): FixtureRenderTarget {
        TODO("not implemented")
    }

    override fun setRenderPlan(fixtureTypeRenderPlan: FixtureTypeRenderPlan?) {
        TODO("not implemented")
    }

    override fun draw() {
        TODO("not implemented")
    }

    override suspend fun finish() {
        TODO("not implemented")
    }

    override fun release() {
        TODO("not implemented")
    }

    override fun logStatus() {
        TODO("not implemented")
    }
}

class RasterRenderEngine(
    gl: GlContext,
    private var width: Int,
    private var height: Int,
    override val fixtureType: FixtureType
) : RenderEngine(gl, LocationStrategy.Continuous), FixtureRenderEngine {
    private lateinit var fixture: Fixture

    private var quad = calcQuad(width, height)
    private var program: GlslProgram? = null

    private fun calcQuad(height: Int, width: Int) =
        gl.runInContext { Quad(gl, listOf(Quad.Rect(height.toFloat(), 0f, 0f, width.toFloat()))) }

    fun useProgram(glslProgram: GlslProgram?) {
        this.program?.release()

        this.program = glslProgram
        glslProgram?.setResolution()
    }

    private fun GlslProgram.setResolution() {
        setPixDimens(width, height)
        setResolution(width.toFloat(), height.toFloat())
    }

    fun onResize(width: Int, height: Int) {
        this.width = width
        this.height = height
        program?.setResolution()
        quad = calcQuad(height, width)
    }

    override fun addFixture(fixture: Fixture): FixtureRenderTarget {
        this.fixture = fixture
        return object : FixtureRenderTarget {
            override val renderEngine: FixtureRenderEngine
                get() = this@RasterRenderEngine
            override val fixture: Fixture
                get() = fixture

            override fun sendFrame(remoteVisualizers: RemoteVisualizers) {
            }

            override fun clearRenderPlan() {
                program = null
            }

            override fun release() {
                clearRenderPlan()
            }
        }
    }

    override fun setRenderPlan(fixtureTypeRenderPlan: FixtureTypeRenderPlan?) {
        if (fixtureTypeRenderPlan == null) {
            useProgram(null)
            return
        }

        fixtureTypeRenderPlan.programs.forEach { programRenderPlan ->
            if (programRenderPlan.renderTargets.any { it.fixture == fixture }) {
                useProgram(programRenderPlan.program)
            }
        }
    }

    override fun logStatus() {
        logger.info { "Rendering ${width}x${height} pixels for ${fixture.title}."}
    }

    override fun onBind(engineFeedContext: EngineFeedContext) {
    }

    override fun beforeRender() {
    }

    override fun bindResults() {
    }

    public override fun render() {
        val program = program ?: return

        gl.check { viewport(0, 0, width, height) }
        gl.check { clearColor(1f, 0f, 0f, 1f) }
        gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

        gl.useProgram(program)
        program.transformUniform?.set(Matrix4F.identity.withRotation(EulerAngle(.1, .1, .1)))
        program.aboutToRenderFrame()
        quad.prepareToRender(program.vertexPositionAttrib) {
            quad.renderRect(0)
        }
    }

    override fun afterRender() {
    }

    override suspend fun awaitResults() {
    }

    override fun onRelease() {
        program?.release()
        quad.release()
    }

    companion object {
        private val logger = Logger<RasterRenderEngine>()
    }
}