package baaahs.glsl

import baaahs.describe
import baaahs.fixtures.PixelArrayDevice
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeKgl
import org.spekframework.spek2.Spek
import kotlin.test.expect

object GuruMeditationErrorSpec : Spek({
    describe<GuruMeditationError> {
        val gl by value { FakeGlContext(FakeKgl()) }
        val guruMeditationError by value { GuruMeditationError(PixelArrayDevice) }

        it("should create a RenderPlan") {
            expect(PixelArrayDevice.errorIndicatorShader) {
                guruMeditationError.linkedPatch.shaderInstance.shader.shader
            }
        }
    }
})