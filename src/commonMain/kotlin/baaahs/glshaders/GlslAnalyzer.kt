package baaahs.glshaders

class GlslAnalyzer {
    fun analyze(shaderText: String): ShaderFragment {
        val statements = findStatements(shaderText)
        val title = Regex("^// (.*)").find(shaderText)?.groupValues?.get(1)
            ?: throw IllegalArgumentException("Shader name must be in a comment on the first line")


        return ShaderFragment(
            title,
            statements.mapNotNull { it.asUniformOrNull() },
            statements.mapNotNull { it.asFunctionOrNull() })
    }

    internal fun findStatements(shaderText: String): List<GlslStatement> {
        val data = Context()
        var state: ParseState = ParseState.initial(data)

        Regex("(.*?)(//|[;{}\n#])").findAll(shaderText).forEach { matchResult ->
            val before = matchResult.groupValues[1]
            val token = matchResult.groupValues[2]

            if (before.isNotEmpty()) state.visitText(before)
            state = state.visit(token)
        }
        state.visitEof()

        return data.statements
    }

    private class Context {
        val defines: MutableMap<String, String> = hashMapOf()
        val statements: MutableList<GlslStatement> = arrayListOf()
        var enabled = true
        val enabledStack = mutableListOf<Boolean>()

        fun doDefine(args: List<String>) {
            if (enabled) {
                when (args.size) {
                    0 -> throw IllegalArgumentException("#define without args?")
                    1 -> defines[args[0]] = ""
                    else -> defines[args[0]] = args.subList(1, args.size).joinToString(" ")
                }
            }
        }

        fun doIfdef(args: List<String>) {
            if (args.size != 1) throw IllegalArgumentException("huh? #ifdef ${args.joinToString(" ")}")
            enabledStack.add(enabled)
            enabled = enabled && defines.containsKey(args.first())
        }

        fun doIfndef(args: List<String>) {
            if (args.size != 1) throw IllegalArgumentException("huh? #ifndef ${args.joinToString(" ")}")
            enabledStack.add(enabled)
            enabled = enabled && !defines.containsKey(args.first())
        }

        fun doElse(args: List<String>) {
            if (args.isNotEmpty()) throw IllegalArgumentException("huh? #else ${args.joinToString(" ")}")
            enabled = enabledStack.last() && !enabled
        }

        fun doEndif(args: List<String>) {
            if (args.isNotEmpty()) throw IllegalArgumentException("huh? #endif ${args.joinToString(" ")}")
            enabled = enabledStack.removeLast()
        }
    }

    private sealed class ParseState(val context: Context) {
        companion object {
            fun initial(context: Context): ParseState = Statement(context)
        }

        private val text = StringBuilder()
        val textAsString get() = text.toString()

        fun appendText(value: String, substitute: Boolean = true) {
            if (substitute) {
                text.append(value.replace(Regex("([A-Za-z][A-Za-z0-9_]*)")) {
                    context.defines[it.value] ?: it.value
                })
            } else {
                text.append(value)
            }
        }

        open fun visit(token: String): ParseState {
            return when (token) {
                "//" -> visitComment()
                ";" -> visitSemicolon()
                "{" -> visitLeftCurlyBrace()
                "}" -> visitRightCurlyBrace()
                "#" -> visitDirective()
                "\n" -> visitNewline()
                else -> throw IllegalStateException("unknown token $token")
            }
        }

        open fun visitText(value: String): ParseState {
            if (context.enabled) appendText(value)
            return this
        }

        open fun visitComment(): ParseState = visitText("//")
        open fun visitSemicolon(): ParseState = visitText(";")
        open fun visitLeftCurlyBrace(): ParseState = visitText("{")
        open fun visitRightCurlyBrace(): ParseState = visitText("}")
        open fun visitDirective(): ParseState = visitText("#")
        open fun visitNewline(): ParseState = visitText("\n")
        open fun visitEof(): Unit = Unit

        open fun addComment(comment: String) {}

        private class Statement(context: Context) : ParseState(context) {
            val comments = mutableListOf<String>()

            override fun visitComment(): ParseState {
                return Comment(context, this)
            }

            override fun visitSemicolon(): Statement {
                visitText(";")
                finishStatement()
                return Statement(context)
            }

            override fun visitLeftCurlyBrace(): ParseState {
                return Block(context, this).also { visitText("{") }
            }

            override fun visitDirective(): ParseState {
                return Directive(context, this)
            }

            override fun visitEof() {
                if (!isEmpty()) finishStatement()
            }

            override fun addComment(comment: String) {
                comments.add(comment)
            }

            fun finishStatement() {
                context.statements.add(GlslStatement(textAsString, comments))
            }

            fun isEmpty(): Boolean = textAsString.isEmpty() && comments.isEmpty()
        }

        private class Comment(
            context: Context,
            val priorParseState: ParseState
        ) : ParseState(context) {
            val parts = mutableListOf<String>()

            override fun visitText(value: String): ParseState {
                parts.add(value)
                return this
            }

            override fun visitNewline(): ParseState {
                priorParseState.addComment(parts.joinToString(""))
                return priorParseState
            }
        }

        private class Block(
            context: Context,
            val priorParseState: Statement
        ) : ParseState(context) {
            var nestLevel: Int = 1

            override fun visitLeftCurlyBrace(): ParseState {
                nestLevel++
                return super.visitLeftCurlyBrace()
            }

            override fun visitRightCurlyBrace(): ParseState {
                nestLevel--
                super.visitRightCurlyBrace()

                return if (nestLevel == 0) {
                    finishStatement()
                    Statement(context)
                } else {
                    this
                }
            }

            override fun visitEof() {
                if (!priorParseState.isEmpty()) finishStatement()
            }

            private fun finishStatement() {
                priorParseState.visitText(textAsString)
                priorParseState.finishStatement()
            }
        }

        private class Directive(context: Context, val priorParseState: ParseState) : ParseState(context) {
            override fun visitText(value: String): ParseState {
                appendText(value, substitute = false)
                return this
            }

            override fun visitNewline(): ParseState {
                val str = textAsString
                val args = str.split(Regex("\\s+")).toMutableList()
                val directive = args.removeFirst()
                when (directive) {
                    "define" -> context.doDefine(args)
                    "ifdef" -> context.doIfdef(args)
                    "ifndef" -> context.doIfndef(args)
                    "else" -> context.doElse(args)
                    "endif" -> context.doEndif(args)
                    else -> throw IllegalArgumentException("unknown directive #$str")
                }
                return priorParseState
            }

            override fun visitEof() {
                visitNewline()
            }
        }
    }

    data class GlslStatement(val text: String, val comments: List<String> = emptyList()) {
        fun asUniformOrNull(): ShaderFragment.GlslUniform? {
            return Regex("uniform\\s+(\\w+)\\s+(\\w+)\\s*;", RegexOption.MULTILINE).find(text)?.let {
                ShaderFragment.GlslUniform(it.groupValues[1], it.groupValues[2])
            }
        }

        fun asFunctionOrNull(): ShaderFragment.GlslFunction? {
            return Regex("(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(\\{[\\s\\S]*})", RegexOption.MULTILINE).find(text)
                ?.let {
                    ShaderFragment.GlslFunction(
                        it.groupValues[1],
                        it.groupValues[2],
                        it.groupValues[3],
                        it.groupValues[4]
                    )
                }
        }
    }
}