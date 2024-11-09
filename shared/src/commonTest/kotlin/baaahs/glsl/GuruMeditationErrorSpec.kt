package baaahs.glsl

import baaahs.describe
import baaahs.device.PixelArrayDevice
import baaahs.kotest.value
import baaahs.show.live.LinkedPatch
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeKgl
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

object GuruMeditationErrorSpec : DescribeSpec({
    describe<GuruMeditationError> {
        val gl by value { FakeGlContext(FakeKgl()) }
        val guruMeditationError by value { GuruMeditationError(PixelArrayDevice) }

        it("should create a RenderPlan") {
            val rootNode = guruMeditationError.linkedProgram.rootNode as LinkedPatch
            rootNode.shader.shader
                .shouldBe(PixelArrayDevice.errorIndicatorShader)
        }
    }
})