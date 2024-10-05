package baaahs.gl.glsl.parser

import baaahs.gl.glsl.*

class Context(
    val tokenizer: Tokenizer
) {
    val macros: MutableMap<String, Macro> = hashMapOf()
    private var macroDepth = 0
    val statements: MutableList<GlslCode.GlslStatement> = arrayListOf()
    var outputEnabled = true
    private var matchedBranch = false
    private val enabledStack = mutableListOf<Boolean>()

    val structs = mutableMapOf<String, GlslCode.GlslStruct>()

    fun findType(name: String): GlslType =
        structs[name]?.let { GlslType.Struct(it) }
            ?: GlslType.from(name)

    fun doUndef(undefToken: Token, args: List<Token>) {
        if (outputEnabled) {
            if (args.size != 1) throw glslError(undefToken, "#undef ${args.joinToString(" ")}")
            macros.remove(args[0].text)
        }
    }

    fun doIf(ifToken: Token, args: List<Token>) {
        if (args.isEmpty()) throw glslError(ifToken, "#if ${args.joinToString(" ")}")
        enabledStack.add(outputEnabled)
        val matches = evaluate(args)
        outputEnabled = outputEnabled && matches
        matchedBranch = matches
    }

    fun doIfdef(ifdefToken: Token, args: List<Token>) {
        if (args.size != 1) throw glslError(ifdefToken, "#ifdef ${args.joinToString(" ")}")
        enabledStack.add(outputEnabled)
        val matches = macros.containsKey(args.first().text)
        outputEnabled = outputEnabled && matches
        matchedBranch = matches
    }

    fun doIfndef(ifndefToken: Token, args: List<Token>) {
        if (args.size != 1) throw glslError(ifndefToken, "#ifndef ${args.joinToString(" ")}")
        enabledStack.add(outputEnabled)
        val matches = !macros.containsKey(args.first().text)
        outputEnabled = outputEnabled && matches
        matchedBranch = matches
    }

    fun doElse(elseToken: Token, args: List<Token>) {
        if (enabledStack.isEmpty()) throw glslError(elseToken, "#else outside of #if/#endif")
        if (args.isNotEmpty()) throw glslError(elseToken, "#else ${args.joinToString(" ")}")
        outputEnabled = !matchedBranch && enabledStack.last() && !outputEnabled
        if (outputEnabled) matchedBranch = true
    }

    fun doElif(elifToken: Token, args: List<Token>) {
        if (enabledStack.isEmpty()) throw glslError(elifToken, "#elif outside of #if/#endif")
        if (args.isEmpty()) throw glslError(elifToken, "#elif ${args.joinToString(" ")}")
        if (enabledStack.last()) {
            val matches = !matchedBranch && evaluate(args)
            outputEnabled = matches
            if (matches) matchedBranch = true
        }
    }

    fun doEndif(endifToken: Token, args: List<Token>) {
        if (enabledStack.isEmpty()) throw glslError(endifToken, "#endif outside of #if")
        if (args.isNotEmpty()) throw glslError(endifToken, "#endif ${args.joinToString(" ")}")
        outputEnabled = enabledStack.removeLast()
        matchedBranch = false
    }

    @Suppress("UNUSED_PARAMETER")
    fun doLine(lineToken: Token, args: List<Token>) {
        // No-op.
    }

    private fun evaluate(args: List<Token>) =
        try {
            GlslMacroExpressionEvaluator.evaluate(args)
        } catch (e: Exception) {
            throw glslError(
                args.firstOrNull(),
                "${e.message ?: "unknown error"} in \"${args.joinToString(" ")}\".")
        }

    fun checkForMacro(token: Token, parseState: ParseState): ParseState? {
        val macro = macros[token.text]
        return when {
            macro == null -> null
            macro.params == null -> {
                macroDepth++
                if (macroDepth >= GlslParser.maxMacroDepth)
                    throw glslError(token, "Max macro depth exceeded for \"${token.text}\".")

                tokenizer.push(macro.replacement)
                parseState
                    .also { macroDepth-- }
            }
            else -> MacroExpansion(this, parseState, macro)
        }
    }

    fun glslError(token: Token?, message: String) =
        AnalysisException(message, token?.lineNumber ?: GlslError.NO_LINE)
}