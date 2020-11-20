package baaahs.gl

import baaahs.FakeClock
import baaahs.fixtures.DeviceTypeRenderPlan
import baaahs.fixtures.ProgramRenderPlan
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.RenderTarget
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.util.Clock
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.dsl.LifecycleAware
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.meta.*
import org.spekframework.spek2.style.specification.Suite
import kotlin.test.assertEquals

fun testPlugins(clock: Clock = FakeClock(0.0)) =
    Plugins.safe(PluginContext(clock))

fun <T> LifecycleAware.override(letValue: T, factory: () -> T) {
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
) = DeviceTypeRenderPlan(listOf(ProgramRenderPlan(glslProgram, renderTargets.toList())))

fun expectStatements(
    expected: List<GlslCode.GlslStatement>,
    actual: () -> List<GlslCode.GlslStatement>,
    checkLineNumbers: Boolean = false
) {
    assertEquals(
        expected.map { it.esc(checkLineNumbers) }.joinToString("\n"),
        actual().map { it.esc(checkLineNumbers) }.joinToString("\n")
    )
}

fun <T> expects(expected: Collection<T>, block: () -> Collection<T>) {
    val actual = block()
    if (actual != expected)
        assertEquals(expected.joinToString("\n"), actual.joinToString("\n"))
}

fun <K, V> expects(expected: Map<K, V>, block: () -> Map<K, V>) {
    val actual = block()
    fun Map<*, *>.prettier() =
        entries.sortedBy { (k, _) -> k.toString() }.joinToString("\n") { (k, v) -> "$k = $v" }
    if (actual != expected)
        assertEquals(expected.prettier(), actual.prettier())
}

@Synonym(SynonymType.TEST)
@Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
fun <T> Suite.expectValue(expected: T, skip: Skip = Skip.No, actual: () -> T) {
    delegate.test("should equal", skip, delegate.defaultTimeout) {
        expect(actual()).toBe(expected)
    }
}

fun <T> undefined(): T = throw NotImplementedError("value not given")