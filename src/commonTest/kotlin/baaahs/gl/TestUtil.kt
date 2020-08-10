package baaahs.gl

import baaahs.gl.glsl.GlslAnalyzer
import org.spekframework.spek2.dsl.LifecycleAware
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.meta.*
import org.spekframework.spek2.style.specification.Suite
import kotlin.test.assertEquals
import kotlin.test.expect


fun <T> LifecycleAware.override(letValue: T, factory: () -> T) {
    value(letValue, factory)
}

fun String.esc() = replace("\n", "\\n")

fun GlslAnalyzer.GlslStatement.esc(lineNumbers: Boolean): String {
    val buf = StringBuilder()
    buf.append(text.trim().esc())
    if (comments.isNotEmpty())
        buf.append(" // ${comments.joinToString(" ") { it.trim().esc() }}")
    if (lineNumbers)
        buf.append(" # $lineNumber")
    return buf.toString()
}

fun expectStatements(
    expected: List<GlslAnalyzer.GlslStatement>,
    actual: () -> List<GlslAnalyzer.GlslStatement>,
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

@Synonym(SynonymType.TEST)
@Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
fun <T> Suite.expectValue(expected: T, skip: Skip = Skip.No, actual: () -> T) {
    delegate.test("should equal", skip, delegate.defaultTimeout) {
        expect(expected, actual)
    }
}

fun <T> undefined(): T = throw NotImplementedError("value not given")