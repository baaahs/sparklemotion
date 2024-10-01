package baaahs.gl.glsl.parser

import baaahs.gl.glsl.AnalysisException
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslMacroExpressionEvaluator
import baaahs.gl.glsl.GlslType

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

    fun doUndef(args: List<String>) {
        if (outputEnabled) {
            if (args.size != 1) throw glslError("#undef ${args.joinToString(" ")}")
            macros.remove(args[0])
        }
    }

    fun doIf(args: List<String>) {
        if (args.isEmpty()) throw glslError("#if ${args.joinToString(" ")}")
        enabledStack.add(outputEnabled)
        val matches = evaluate(args.joinToString(" "))
        outputEnabled = outputEnabled && matches
        matchedBranch = matches
    }

    fun doIfdef(args: List<String>) {
        if (args.size != 1) throw glslError("#ifdef ${args.joinToString(" ")}")
        enabledStack.add(outputEnabled)
        val matches = macros.containsKey(args.first())
        outputEnabled = outputEnabled && matches
        matchedBranch = matches
    }

    fun doIfndef(args: List<String>) {
        if (args.size != 1) throw glslError("#ifndef ${args.joinToString(" ")}")
        enabledStack.add(outputEnabled)
        val matches = !macros.containsKey(args.first())
        outputEnabled = outputEnabled && matches
        matchedBranch = matches
    }

    fun doElse(args: List<String>) {
        if (enabledStack.isEmpty()) throw glslError("#else outside of #if/#endif")
        if (args.isNotEmpty()) throw glslError("#else ${args.joinToString(" ")}")
        outputEnabled = !matchedBranch && enabledStack.last() && !outputEnabled
        if (outputEnabled) matchedBranch = true
    }

    fun doElif(args: List<String>) {
        if (enabledStack.isEmpty()) throw glslError("#elif outside of #if/#endif")
        if (args.isEmpty()) throw glslError("#elif ${args.joinToString(" ")}")
        if (enabledStack.last()) {
            val matches = !matchedBranch && evaluate(args.joinToString(" "))
            outputEnabled = matches
            if (matches) matchedBranch = true
        }
    }

    fun doEndif(args: List<String>) {
        if (enabledStack.isEmpty()) throw glslError("#endif outside of #if")
        if (args.isNotEmpty()) throw glslError("#endif ${args.joinToString(" ")}")
        outputEnabled = enabledStack.removeLast()
        matchedBranch = false
    }

    @Suppress("UNUSED_PARAMETER")
    fun doLine(args: List<String>) {
        // No-op.
    }

    private fun evaluate(args: String) =
        try {
            GlslMacroExpressionEvaluator.evaluate(args)
        } catch (e: Exception) {
            throw glslError(e.message!!)
        }

    fun checkForMacro(token: Token, parseState: ParseState): ParseState? {
        val macro = macros[token.text]
        return when {
            macro == null -> null
            macro.params == null -> {
                macroDepth++
                if (macroDepth >= GlslParser.maxMacroDepth)
                    throw glslError("Max macro depth exceeded for \"${token.text}\".")

                tokenizer.push(macro.replacement)
                parseState
                    .also { macroDepth-- }
            }
            else -> MacroExpansion(this, parseState, macro)
        }
    }

    fun glslError(message: String) =
        AnalysisException(message, tokenizer.lineNumberForError)
}