package baaahs.glsl

import baaahs.TestModel
import baaahs.describe
import baaahs.gl.render.RenderManager
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeKgl
import org.spekframework.spek2.Spek
import kotlin.test.assertNotNull

object GuruMeditationErrorSpec : Spek({
    describe<GuruMeditationError> {
        val gl by value { FakeGlContext(FakeKgl()) }
        val renderManager by value { RenderManager(TestModel) { gl } }
        val renderPlan by value { GuruMeditationError.createRenderPlan(renderManager) }

        it("should create a RenderPlan") {
            assertNotNull(renderPlan)
        }
    }
})