package baaahs.plugin

import baaahs.FakeClock
import baaahs.describe
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.AutoWirer
import baaahs.gl.testPlugins
import baaahs.glsl.Shaders
import baaahs.only
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object CorePluginSpek : Spek({
    describe<CorePlugin.TimeDataSource> {
        val clock by value { FakeClock(0.0) }
        val dataSource by value { CorePlugin.TimeDataSource() }
        val plugins by value { testPlugins(clock) }
        val feed by value { dataSource.createFeed(FakeShowPlayer(plugins = plugins), "time") }
        val gl by value { FakeGlContext() }
        val glFeed by value { feed.bind(gl) }
        val program by value {
            val autoWirer = AutoWirer(plugins)
            val linkedPatch = autoWirer.autoWire(Shaders.red).acceptSuggestedLinkOptions()
                .resolve().openForPreview(autoWirer)!!
            GlslProgram(gl, linkedPatch) { _, _ -> null }
        }
        val programFeed by value { glFeed.bind(program) }

        it("should pass the time as a float, truncating high bits") {
            clock.time = 1234567890.1234
            gl.runInContext { programFeed.setOnProgram() }

            val glProgram = gl.programs.only("program")
            expect(glProgram.getUniform("in_time")).toBe(7890.1234f)
        }
    }
})