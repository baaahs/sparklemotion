package baaahs.glsl

import baaahs.describe
import baaahs.fixtures.PixelArrayDevice
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeKgl
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object GuruMeditationErrorSpec : Spek({
    describe<GuruMeditationError> {
        val gl by value { FakeGlContext(FakeKgl()) }
        val guruMeditationError by value { GuruMeditationError(PixelArrayDevice) }

        it("should create a RenderPlan") {
            expect(guruMeditationError.linkedPatch.shaderInstance.shader.shader)
                .toBe(PixelArrayDevice.errorIndicatorShader)
        }
    }
})