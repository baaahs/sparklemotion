package baaahs.glsl

import baaahs.describe
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeKgl
import org.spekframework.spek2.Spek
import kotlin.test.assertNotNull

object GuruMeditationErrorSpec : Spek({
    describe<GuruMeditationError> {
        val gl by value { FakeGlContext(FakeKgl()) }
        val renderPlan by value { GuruMeditationError.createRenderPlan(gl) }

        it("should create a RenderPlan") {
            assertNotNull(renderPlan)
        }
    }
})