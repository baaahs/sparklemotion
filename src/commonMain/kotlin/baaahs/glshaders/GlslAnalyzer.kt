package baaahs.glshaders

import baaahs.glshaders.GlslCode.GlslFunction
import baaahs.glshaders.GlslCode.GlslVar

class GlslAnalyzer {
    fun analyze(shaderText: String): GlslCode {
        val statements = findStatements(shaderText)
        val title = Regex("^// (.*)").find(shaderText)?.groupValues?.get(1)
            ?: "Unknown"
//            ?: throw IllegalArgumentException("Shader name must be in a comment on the first line")

        return GlslCode(title, null, statements)
    }

    fun asShader(shaderText: String): ShaderFragment {
        val glslObj = analyze(shaderText)

        return ShaderFragment.tryColorShader(glslObj)
            ?: throw IllegalArgumentException("huh? unknown sort of shader")
    }

    internal fun findStatements(shaderText: String): List<GlslStatement> {
        val context = Context()
        var state: ParseState = ParseState.initial(context)

        Regex("(.*?)(//|[;{}\n#])").findAll(shaderText).forEach { matchResult ->
            val before = matchResult.groupValues[1]
            val token = matchResult.groupValues[2]

            if (token == "\n") context.lineNumber++

            if (before.isNotEmpty()) state.visitText(before)
            state = state.visit(token)
        }
        state.visitEof()

        return context.statements
    }

    companion object {
        val wordRegex = Regex("([A-Za-z][A-Za-z0-9_]*)")
    }

    private class Context {
        val defines: MutableMap<String, String> = hashMapOf()
        val statements: MutableList<GlslStatement> = arrayListOf()
        var outputEnabled = true
        val enabledStack = mutableListOf<Boolean>()
        var lineNumber = 1

        fun doDefine(args: List<String>) {
            if (outputEnabled) {
                when (args.size) {
                    0 -> throw IllegalArgumentException("#define without args?")
                    1 -> defines[args[0]] = ""
                    else -> defines[args[0]] = args.subList(1, args.size).joinToString(" ")
                }
            }
        }

        fun doUndef(args: List<String>) {
            if (outputEnabled) {
                if (args.size != 1) throw IllegalArgumentException("huh? #undef ${args.joinToString(" ")}")
                defines.remove(args[0])
            }
        }

        fun doIfdef(args: List<String>) {
            if (args.size != 1) throw IllegalArgumentException("huh? #ifdef ${args.joinToString(" ")}")
            enabledStack.add(outputEnabled)
            outputEnabled = outputEnabled && defines.containsKey(args.first())
        }

        fun doIfndef(args: List<String>) {
            if (args.size != 1) throw IllegalArgumentException("huh? #ifndef ${args.joinToString(" ")}")
            enabledStack.add(outputEnabled)
            outputEnabled = outputEnabled && !defines.containsKey(args.first())
        }

        fun doElse(args: List<String>) {
            if (args.isNotEmpty()) throw IllegalArgumentException("huh? #else ${args.joinToString(" ")}")
            outputEnabled = enabledStack.last() && !outputEnabled
        }

        fun doEndif(args: List<String>) {
            if (args.isNotEmpty()) throw IllegalArgumentException("huh? #endif ${args.joinToString(" ")}")
            outputEnabled = enabledStack.removeLast()
        }

        @Suppress("UNUSED_PARAMETER")
        fun doLine(args: List<String>) {
            // No-op.
        }
    }

    private sealed class ParseState(val context: Context) {
        companion object {
            fun initial(context: Context): ParseState = Statement(context)
        }

        private val text = StringBuilder()
        val textAsString get() = text.toString()
        fun textIsEmpty() = text.isEmpty()

        fun appendText(value: String) {
            text.append(value)
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
            if (context.outputEnabled || value == "\n") {
                val substituted = value.replace(wordRegex) {
                    context.defines[it.value] ?: it.value
                }

                appendText(substituted)
            }
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
            var lineNumber = context.lineNumber
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

            override fun visitNewline(): ParseState {
                return if (textIsEmpty() && comments.isEmpty()) {
                    // Skip leading newlines.
                    lineNumber = context.lineNumber
                    this
                } else {
                    super.visitNewline()
                }
            }

            override fun visitEof() {
                if (!isEmpty()) finishStatement()
            }

            override fun addComment(comment: String) {
                comments.add(comment)
            }

            fun finishStatement() {
                context.statements.add(GlslStatement(textAsString, comments, lineNumber))
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

            override fun visitDirective(): ParseState {
                return Directive(context, this)
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
                appendText(value)
                return this
            }

            override fun visitNewline(): ParseState {
                val str = textAsString
                val args = str.split(Regex("\\s+")).toMutableList()
                val directive = args.removeFirst()
                when (directive) {
                    "define" -> context.doDefine(args)
                    "undef" -> context.doUndef(args)
                    "ifdef" -> context.doIfdef(args)
                    "ifndef" -> context.doIfndef(args)
                    "else" -> context.doElse(args)
                    "endif" -> context.doEndif(args)
                    "line" -> context.doLine(args)
                    else -> throw IllegalArgumentException("unknown directive #$str")
                }
                priorParseState.visitText("\n")
                return priorParseState
            }

            override fun visitEof() {
                visitNewline()
            }
        }
    }

    data class GlslStatement(
        val text: String,
        val comments: List<String> = emptyList(),
        val lineNumber: Int? = null
    ) {
        fun asSpecialOrNull(): GlslCode.GlslOther? {
            return Regex("^(struct|precision)\\s+.*;", RegexOption.MULTILINE)
                .find(text.trim())?.let {
                    val (keyword) = it.destructured
                    GlslCode.GlslOther(keyword, text, lineNumber)
                }
        }

        fun asVarOrNull(): GlslVar? {
            // If there are curly braces it must be a function.
            if (text.contains("{")) return null

            return Regex("((uniform|const)\\s+)?(\\w+)\\s+(\\w+)\\s*(\\s*.*);", RegexOption.MULTILINE)
                .find(text.trim())?.let {
                    val (_, qualifier, type, name, constValue) = it.destructured
                    if (constValue.contains("(")) return null // function declaration
                    var (isConst, isUniform) = (false to false)
                    when (qualifier) {
                        "const" -> isConst = true
                        "uniform" -> isUniform = true
                    }
                    GlslVar(type, name, text, isConst, isUniform, lineNumber)
                }
        }

        fun asFunctionOrNull(globalVars: Set<String> = emptySet()): GlslFunction? {
            return Regex("(\\w+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(\\{[\\s\\S]*})", RegexOption.MULTILINE)
                .find(text.trim())?.let {
                    val (returnType, name, params, body) = it.destructured
                    GlslFunction(returnType, name, params, text, lineNumber, globalVars)
                }
        }
    }
}