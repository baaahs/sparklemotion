package baaahs.util

import java.io.FilterWriter
import java.io.Writer

class IndentingWriter(
    out: Writer,
    private val indent: String = "    "
) : FilterWriter(out) {
    @Suppress("MemberVisibilityCanBePrivate")
    var indentLevel = 0

    private var lastCharWasNewLine = true

    override fun write(c: Int) {
        if (c != NEWLINE && lastCharWasNewLine) writeIndent()
        super.write(c)
        lastCharWasNewLine = c == NEWLINE
    }

    override fun write(cbuf: CharArray, off: Int, len: Int) {
        write(cbuf.toString(), off, len)
    }

    override fun write(str: String, off: Int, len: Int) {
        if (len == 0) return

        var lastChunkWasEmpty = false
        str.substring(off, len)
            .splitToSequence("\n")
            .forEachIndexed { index, chunk ->
                val startsWithNewline = index > 0
                if (startsWithNewline) super.write(NEWLINE)
                if (lastCharWasNewLine || startsWithNewline) writeIndent()
                val length = chunk.length
                super.write(chunk, 0, length)
                if (length == 0) lastChunkWasEmpty = true
            }
        lastCharWasNewLine = lastChunkWasEmpty
    }

    private fun writeIndent() {
        super.write(indent.repeat(indentLevel), 0, indent.length * indentLevel)
    }

    fun indent(block: () -> Unit) {
        indentLevel++
        try { block() } finally { indentLevel-- }
    }

    companion object {
        const val NEWLINE = '\n'.code
    }
}