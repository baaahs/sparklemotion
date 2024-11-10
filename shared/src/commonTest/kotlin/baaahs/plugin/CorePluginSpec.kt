package baaahs.plugin

import baaahs.FakeClock
import baaahs.describe
import baaahs.gl.RootToolchain
import baaahs.gl.autoWire
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.ContentType
import baaahs.gl.testPlugins
import baaahs.glsl.Shaders
import baaahs.kotest.value
import baaahs.only
import baaahs.plugin.core.feed.TimeFeed
import baaahs.show.mutable.MutablePatchSet
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import baaahs.util.asInstant
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

class CorePluginSpec : DescribeSpec({
    describe<TimeFeed> {
        val clock by value { FakeClock(0.0) }
        val feed by value { TimeFeed() }
        val toolchain by value { RootToolchain(testPlugins(clock) ) }
        val feedContext by value { feed.open(FakeShowPlayer(toolchain = toolchain), "time") }
        val gl by value { FakeGlContext() }
        val glFeed by value { feedContext.bind(gl) }
        val program by value {
            val mutablePatch = toolchain.autoWire(Shaders.red)
                .acceptSuggestedLinkOptions()
                .confirm()
            val linkedPatch = MutablePatchSet(mutablePatch)
                .openForPreview(toolchain, ContentType.Color)!!
            GlslProgram.create(gl, linkedPatch) { _, _ -> null }
        }
        val programFeed by value { glFeed.bind(program) }

        it("should pass the time as a float, truncating high bits") {
            clock.time = 1234567890.1234.asInstant()
            gl.runInContext { programFeed.setOnProgram() }

            val glProgram = gl.programs.only("program")
            glProgram.getUniform<Float>("in_time").shouldBe(7890.1235f)
        }
    }
})