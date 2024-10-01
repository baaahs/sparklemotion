package baaahs.gl.glsl.parser

import baaahs.gl.glsl.AnalysisException
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType

class Struct(context: Context) : Statement(context) {
    override fun createStatement(): GlslCode.GlslStatement =
        asStructOrNull()
            ?: throw context.glslError("huh? couldn't find a struct in \"$tokensAsString\"")

    fun asStructOrNull(): GlslCode.GlslStruct? {
        val text = tokensAsString

        // Escaped closing brace/bracket required for Kotlin 1.5+/JS or we fail with "lone quantifier brackets".
        @Suppress("RegExpRedundantEscape")
        return Regex("^(uniform\\s+)?struct\\s+(\\w+)\\s+\\{([^}]+)\\}(?:\\s+(\\w+)?)?;\$", RegexOption.MULTILINE)
            .find(text.trim())?.let { match ->
                val (uniform, name, members, varName) = match.destructured
                val fields = mutableMapOf<String, GlslType>()

                members.replace(Regex("//.*"), "")
                    .split(";")
                    .forEach { member ->
                        val trimmed = member.trim()
                        if (trimmed.isEmpty()) return@forEach
                        val parts = trimmed.split(Regex("\\s+"))
                        when (parts.size) {
                            0 -> return@forEach
                            2 -> fields[parts[1]] = GlslType.from(parts[0])
                            else -> throw AnalysisException("illegal struct member \"$member\"", lineNumber)
                        }
                    }
                val varNameOrNull = varName.ifBlank { null }
                GlslCode.GlslStruct(
                    name, fields, varNameOrNull, uniform.isNotBlank(),
                    text, lineNumber, comments
                )
                    .also { context.structs[name] = it }
            }
    }
}