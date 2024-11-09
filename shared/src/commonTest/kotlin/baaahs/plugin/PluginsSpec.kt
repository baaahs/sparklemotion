package baaahs.plugin

import baaahs.FakeClock
import baaahs.StubPubSub
import baaahs.describe
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.kotest.value
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
object PluginsSpec : DescribeSpec({
    describe<Plugins> {
        val plugins by value { Plugins.buildForClient(PluginContext(FakeClock(), StubPubSub()), emptyList()) }

        it("should resolve content types") {
            expect(plugins.resolveContentType("color"))
                .toEqual(Color)
        }
    }
})