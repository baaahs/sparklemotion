package baaahs.gl

import baaahs.FakeClock
import baaahs.StubPubSub
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.fixtures.ProgramRenderPlan
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.RenderTarget
import baaahs.kotest.value
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.show.mutable.MutablePatch
import baaahs.util.Clock
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope
import io.kotest.matchers.*
import kotlin.test.assertEquals

fun testPlugins(clock: Clock = FakeClock()) =
    Plugins.safe(PluginContext(clock, StubPubSub()))

fun <T> ContainerScope.override(letValue: T, factory: () -> T) {
    value(letValue, factory)
}

fun String.esc() = replace("\n", "\\n")

fun GlslCode.GlslStatement.esc(lineNumbers: Boolean): String {
    val buf = StringBuilder()
    buf.append(fullText.trim().esc())
    if (comments.isNotEmpty())
        buf.append(" // ${comments.joinToString(" ") { it.trim().esc() }}")
    if (lineNumbers)
        buf.append(" # $lineNumber")
    return buf.toString()
}

fun renderPlanFor(
    glslProgram: GlslProgram,
    vararg renderTargets: RenderTarget
) = FixtureTypeRenderPlan(listOf(ProgramRenderPlan(glslProgram, renderTargets.toList())))

fun expectStatements(
    expected: List<GlslCode.GlslStatement>,
    actual: () -> List<GlslCode.GlslStatement>,
    checkLineNumbers: Boolean = false
) {
    assertEquals(
        expected.joinToString("\n") { it.esc(checkLineNumbers) },
        actual().joinToString("\n") { it.esc(checkLineNumbers) }
    )
}

fun <T> expects(expected: Collection<T>, block: () -> Collection<T>) {
    val actual = block()
    fun T.prettier() =
        if (this is MutablePatch) {
            "MutablePatch(\n" +
                    "    mutableShader=$mutableShader\n" +
                    "    incomingLinks=\n" +
                    "${incomingLinks.prettier("        ")}\n" +
                    "    stream=$stream\n" +
                    "    priority=$priority\n" +
                    ")"
        } else this.toString()

    if (actual != expected)
        assertEquals(
            expected.joinToString("\n") { it.prettier() },
            actual.joinToString("\n") { it.prettier() }
        )
}

fun Map<*, *>.prettier(pfx: String = "") =
    entries.sortedBy { (k, _) -> k.toString() }.joinToString("\n") { (k, v) -> "$pfx$k: $v" }

fun <K, V> expects(expected: Map<K, V>, block: () -> Map<K, V>) {
    val actual = block()
    if (actual != expected)
        assertEquals(expected.prettier(), actual.prettier())
}

suspend fun <T> DescribeSpecContainerScope.expectValue(expected: T, actual: () -> T) {
    it("should equal") {
        actual().shouldBe(expected)
    }
}

fun <T> undefined(): T = throw NotImplementedError("value not given")
