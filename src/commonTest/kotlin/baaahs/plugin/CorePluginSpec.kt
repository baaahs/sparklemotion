package baaahs.plugin

import baaahs.FakeClock
import baaahs.describe
import baaahs.gl.RootToolchain
import baaahs.gl.autoWire
import baaahs.gl.glsl.GlslProgramImpl
import baaahs.gl.patch.ContentType
import baaahs.gl.testPlugins
import baaahs.glsl.Shaders
import baaahs.only
import baaahs.plugin.core.datasource.TimeDataSource
import baaahs.show.mutable.MutablePatchSet
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object CorePluginSpec : Spek({
    describe<TimeDataSource> {
        val clock by value { FakeClock(0.0) }
        val dataSource by value { TimeDataSource() }
        val toolchain by value { RootToolchain(testPlugins(clock) ) }
        val feed by value { dataSource.open(FakeShowPlayer(toolchain = toolchain), "time") }
        val gl by value { FakeGlContext() }
        val glFeed by value { feed.bind(gl) }
        val program by value {
            val mutablePatch = toolchain.autoWire(Shaders.red)
                .acceptSuggestedLinkOptions()
                .confirm()
            val linkedPatch = MutablePatchSet(mutablePatch)
                .openForPreview(toolchain, ContentType.Color)!!
            GlslProgramImpl(gl, linkedPatch) { _, _ -> null }
        }
        val programFeed by value { glFeed.bind(program) }

        it("should pass the time as a float, truncating high bits") {
            clock.time = 1234567890.1234
            gl.runInContext { programFeed.setOnProgram() }

            val glProgram = gl.programs.only("program")
            expect(glProgram.getUniform<Float>("in_time")).toBe(7890.1234f)
        }
    }
})