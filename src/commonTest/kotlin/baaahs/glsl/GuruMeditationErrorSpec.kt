package baaahs.glsl

import TestModel
import baaahs.describe
import baaahs.fixtures.PixelArrayDevice
import baaahs.gl.render.RenderEngine
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeKgl
import org.spekframework.spek2.Spek
import kotlin.test.assertNotNull

object GuruMeditationErrorSpec : Spek({
    describe<GuruMeditationError> {
        val gl by value { FakeGlContext(FakeKgl()) }
        val renderEngine by value { RenderEngine(gl, TestModel, PixelArrayDevice) }
        val renderPlan by value { GuruMeditationError.createRenderPlan(renderEngine) }

        it("should create a RenderPlan") {
            assertNotNull(renderPlan)
        }
    }
})