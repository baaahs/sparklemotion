package baaahs.glsl

import baaahs.describe
import baaahs.device.PixelArrayDevice
import baaahs.show.live.LinkedPatch
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
            val rootNode = guruMeditationError.linkedProgram.rootNode as LinkedPatch
            expect(rootNode.shader.shader)
                .toBe(PixelArrayDevice.errorIndicatorShader)
        }
    }
})