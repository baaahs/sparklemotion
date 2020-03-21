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
        val data = Data()
        var state: ParseState = ParseState.initial(data)

        Regex("(.*?)(//|[;{}\n])").findAll(shaderText).forEach { matchResult ->
            val before = matchResult.groupValues[1]
            val token = matchResult.groupValues[2]

            state.visitText(before)
            state = state.visit(token)
        }
        state.visitEof()

        return data.statements
    }

    data class Data(
        val statements: MutableList<GlslStatement> = arrayListOf(),
        val uniforms: MutableList<ShaderFragment.GlslUniform> = arrayListOf(),
        val functions: MutableList<ShaderFragment.GlslFunction> = arrayListOf()
    )

    sealed class ParseState(val data: Data) {
        companion object {
            fun initial(data: Data): ParseState = Statement(data)
        }

        open fun visit(token: String): ParseState {
            return when (token) {
                "//" -> visitComment()
                ";" -> visitSemicolon()
                "{" -> visitLeftCurlyBrace()
                "}" -> visitRightCurlyBrace()
                "\n" -> visitNewline()
                else -> throw IllegalStateException("unknown token $token")
            }
        }

        abstract fun visitText(value: String): ParseState
        open fun visitComment(): ParseState = visitText("//")
        open fun visitSemicolon(): ParseState = visitText(";")
        open fun visitLeftCurlyBrace(): ParseState = visitText("{")
        open fun visitRightCurlyBrace(): ParseState = visitText("}")
        open fun visitNewline(): ParseState = visitText("\n")
        open fun visitEof(): Unit = Unit

        open fun addComment(comment: String) {}

        private class Statement(data: Data) : ParseState(data) {
            val parts = mutableListOf<String>()
            val comments = mutableListOf<String>()

            override fun visitText(value: String): Statement {
                parts.add(value)
                return this
            }

            override fun visitComment(): ParseState {
                return Comment(data, this)
            }

            override fun visitSemicolon(): Statement {
                visitText(";")
                finishStatement()
                return Statement(data)
            }

            override fun visitLeftCurlyBrace(): ParseState {
                return Block(data, this)
            }

            override fun addComment(comment: String) {
                comments.add(comment)
            }

            override fun visitEof() {
                if (!isEmpty()) finishStatement()
            }

            fun finishStatement() {
                data.statements.add(GlslStatement(parts.joinToString(""), comments))
            }

            fun isEmpty(): Boolean = parts.isEmpty() && comments.isEmpty()
        }

        private class Comment(
            data: Data,
            val priorParseState: ParseState
        ) : ParseState(data) {
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
            data: Data,
            val priorParseState: Statement
        ) : ParseState(data) {
            var nestLevel: Int = 1
            val contents = mutableListOf("{")

            override fun visitText(value: String): ParseState {
                contents.add(value)
                return this
            }

            override fun visitLeftCurlyBrace(): ParseState {
                nestLevel++
                return super.visitLeftCurlyBrace()
            }

            override fun visitRightCurlyBrace(): ParseState {
                nestLevel--
                super.visitRightCurlyBrace()

                return if (nestLevel == 0) {
                    finishStatement()
                    Statement(data)
                } else {
                    this
                }
            }

            override fun visitEof() {
                if (!priorParseState.isEmpty()) finishStatement()
            }

            private fun finishStatement() {
                priorParseState.visitText(contents.joinToString(""))
                priorParseState.finishStatement()
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
            return Regex("(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(\\{[\\s\\S]*})", RegexOption.MULTILINE).find(text)?.let {
                ShaderFragment.GlslFunction(it.groupValues[1], it.groupValues[2], it.groupValues[3], it.groupValues[4])
            }
        }
    }
}