package baaahs.gl.patch

import baaahs.describe
import baaahs.gl.openShader
import baaahs.gl.patch.PortDiagram.ChannelEntry
import baaahs.gl.shader.type.FilterShader
import baaahs.gl.testToolchain
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.live.OpenPatch
import baaahs.show.live.fakeShader
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import kotlin.test.assertTrue

object PortDiagramSpec : Spek({
    describe<PortDiagram> {
        context("shader prioritization") {
            fun Shader.enliven(links: Map<String, OpenPatch.Link> = emptyMap()) =
                OpenPatch(testToolchain.openShader(this), links, ShaderChannel.Main, 0f)

            val shaderA by value { fakeShader("Shader A").enliven() }
            val shaderB by value { fakeShader("Shader B").enliven() }
            val filterShaderA by value {
                fakeShader("Shader A", FilterShader)
                    .enliven(mapOf("inColor" to OpenPatch.ShaderChannelLink(ShaderChannel.Main)))
            }

            it("shaders with higher priority always come first") {
                ChannelEntry(shaderA, priority = 1f, level = 0)
                    .shouldComeBefore(ChannelEntry(shaderB, priority = 0f, level = 0))

                ChannelEntry(shaderB, priority = 1f, level = 0)
                    .shouldComeBefore(ChannelEntry(shaderA, priority = 0f, level = 0))

                ChannelEntry(shaderA, priority = 1f, level = 0)
                    .shouldComeBefore(ChannelEntry(shaderB, priority = 0f, level = 10))

                ChannelEntry(shaderA, priority = 1f, level = 0)
                    .shouldComeBefore(ChannelEntry(filterShaderA, priority = 0f, level = 0))

            }

            it("shaders with the same priority at a lower level in the tree come first") {
                ChannelEntry(shaderA, priority = 0f, level = 1)
                    .shouldComeBefore(ChannelEntry(shaderB, priority = 0f, level = 0))

                ChannelEntry(shaderB, priority = 0f, level = 1)
                    .shouldComeBefore(ChannelEntry(shaderA, priority = 0f, level = 0))
            }

            it("filter shaders with the same priority should come before non-filter shaders") {
                ChannelEntry(filterShaderA, priority = 0f, level = 0)
                    .shouldComeBefore(ChannelEntry(shaderA, priority = 0f, level = 0))

                ChannelEntry(filterShaderA, priority = 0f, level = 0)
                    .shouldComeBefore(ChannelEntry(shaderA, priority = 0f, level = 1))
            }

            it("when all other aspects are equal, sort by lexical order of shader name") {
                ChannelEntry(shaderA, priority = 0f, level = 0)
                    .shouldComeBefore(ChannelEntry(shaderB, priority = 0f, level = 0))
            }
        }
    }
})

fun ChannelEntry.shouldComeBefore(other: ChannelEntry) {
    val comparison = PortDiagram.Candidates.comparator.compare(this, other)
    assertTrue("${this.openPatch.title} should come before ${other.openPatch.title}, " +
            "but ${if (comparison == 0) "they are equivalent" else "it comes after"}") { comparison == -1 }

    // Verify that we're actually sorting using that comparator.
    expect(PortDiagram.Candidates(listOf(this, other)).iterator().asSequence().toList())
        .containsExactly(this.openPatch, other.openPatch)
}