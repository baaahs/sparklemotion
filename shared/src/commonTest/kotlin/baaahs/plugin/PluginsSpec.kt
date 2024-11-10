package baaahs.plugin

import baaahs.FakeClock
import baaahs.StubPubSub
import baaahs.describe
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

@Suppress("unused")
class PluginsSpec : DescribeSpec({
    describe<Plugins> {
        val plugins by value { Plugins.buildForClient(PluginContext(FakeClock(), StubPubSub()), emptyList()) }

        it("should resolve content types") {
            plugins.resolveContentType("color")
                .shouldBe(Color)
        }
    }
})