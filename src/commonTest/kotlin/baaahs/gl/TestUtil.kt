package baaahs.gl

import baaahs.gl.glsl.GlslCode
import baaahs.show.mutable.MutableShaderInstance
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

fun GlslCode.GlslStatement.esc(lineNumbers: Boolean): String {
    val buf = StringBuilder()
    buf.append(fullText.trim().esc())
    if (comments.isNotEmpty())
        buf.append(" // ${comments.joinToString(" ") { it.trim().esc() }}")
    if (lineNumbers)
        buf.append(" # $lineNumber")
    return buf.toString()
}

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

fun Any?.prettify(nestLevel: Int = 0): String {
    val prefix = "  ".repeat(nestLevel)
    return when (this) {
        null -> "null"

        is Collection<*> -> joinToString("") {
            "\n${prefix}- ${it.prettify(nestLevel + 1)}}"
        }

        is Map<*, *> -> entries.sortedBy { it.key.toString() }.joinToString("") { (k,v) ->
            "\n${prefix}- ${k.prettify(nestLevel + 1)}: ${v.prettify(nestLevel + 1)}"
        }

        is MutableShaderInstance ->
            "${prefix}MutableShaderInstance\n" +
                    "$prefix  shader=$mutableShader\n" +
                    "$prefix  incomingLinks:${incomingLinks.prettify(nestLevel + 1)}"

        else -> this.toString()
    }
}

fun <T> expects(expected: Collection<T>, block: () -> Collection<T>) {
    val actual = block()
    if (actual != expected)
        assertEquals(expected.prettify(), actual.prettify())
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
        expect(expected, actual)
    }
}

fun <T> undefined(): T = throw NotImplementedError("value not given")