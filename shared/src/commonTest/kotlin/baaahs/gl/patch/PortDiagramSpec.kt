package baaahs.gl.patch

import baaahs.describe
import baaahs.gl.openShader
import baaahs.gl.patch.PortDiagram.TrackEntry
import baaahs.gl.shader.type.FilterShader
import baaahs.gl.testToolchain
import baaahs.show.Shader
import baaahs.show.Stream
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
                OpenPatch(testToolchain.openShader(this), links, Stream.Main, 0f)

            val shaderA by value { fakeShader("Shader A").enliven() }
            val shaderB by value { fakeShader("Shader B").enliven() }
            val filterShaderA by value {
                fakeShader("Shader A", FilterShader)
                    .enliven(mapOf("inColor" to OpenPatch.StreamLink(Stream.Main)))
            }

            it("shaders with higher priority always come first") {
                TrackEntry(shaderA, priority = 1f, level = 0)
                    .shouldComeBefore(TrackEntry(shaderB, priority = 0f, level = 0))

                TrackEntry(shaderB, priority = 1f, level = 0)
                    .shouldComeBefore(TrackEntry(shaderA, priority = 0f, level = 0))

                TrackEntry(shaderA, priority = 1f, level = 0)
                    .shouldComeBefore(TrackEntry(shaderB, priority = 0f, level = 10))

                TrackEntry(shaderA, priority = 1f, level = 0)
                    .shouldComeBefore(TrackEntry(filterShaderA, priority = 0f, level = 0))

            }

            it("shaders with the same priority at a lower level in the tree come first") {
                TrackEntry(shaderA, priority = 0f, level = 1)
                    .shouldComeBefore(TrackEntry(shaderB, priority = 0f, level = 0))

                TrackEntry(shaderB, priority = 0f, level = 1)
                    .shouldComeBefore(TrackEntry(shaderA, priority = 0f, level = 0))
            }

            it("filter shaders with the same priority should come before non-filter shaders") {
                TrackEntry(filterShaderA, priority = 0f, level = 0)
                    .shouldComeBefore(TrackEntry(shaderA, priority = 0f, level = 0))

                TrackEntry(filterShaderA, priority = 0f, level = 0)
                    .shouldComeBefore(TrackEntry(shaderA, priority = 0f, level = 1))
            }

            it("when all other aspects are equal, sort by lexical order of shader name") {
                TrackEntry(shaderA, priority = 0f, level = 0)
                    .shouldComeBefore(TrackEntry(shaderB, priority = 0f, level = 0))
            }
        }
    }
})

fun TrackEntry.shouldComeBefore(other: TrackEntry) {
    val comparison = PortDiagram.Candidates.comparator.compare(this, other)
    assertTrue("${this.openPatch.title} should come before ${other.openPatch.title}, " +
            "but ${if (comparison == 0) "they are equivalent" else "it comes after"}") { comparison == -1 }

    // Verify that we're actually sorting using that comparator.
    expect(PortDiagram.Candidates(listOf(this, other)).iterator().asSequence().toList())
        .containsExactly(this.openPatch, other.openPatch)
}