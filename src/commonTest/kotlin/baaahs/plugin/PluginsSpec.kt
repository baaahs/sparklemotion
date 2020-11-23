package baaahs.plugin

import baaahs.FakeClock
import baaahs.describe
import baaahs.gl.patch.ContentType
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object PluginsSpec : Spek({
    describe<Plugins> {
        val plugins by value { Plugins(listOf(CorePlugin), PluginContext(FakeClock())) }

        it("should resolve content types") {
            expect(plugins.resolveContentType("color-stream"))
                .toEqual(ContentType.ColorStream)
        }
    }
})