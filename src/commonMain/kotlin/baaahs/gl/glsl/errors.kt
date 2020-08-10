package baaahs.gl.glsl

abstract class GlslException(message: String) : Exception(message) {
    abstract val errors: List<GlslError>
}

class LinkException(
    message: String, row: Int = -1
) : GlslException("Shader link error: $message") {
    override val errors = listOf(GlslError(message, row))
}

class AnalysisException(
    message: String, row: Int = -1
) : GlslException("Shader analysis error: $message") {
    override val errors = listOf(GlslError(message, row))
}

class CompilationException(
    errorMessage: String
) : GlslException("GLSL compilation error: $errorMessage") {
    override val errors = errorMessage.trimEnd().split("\n").map { line ->
        pattern.matchEntire(line)?.groupValues?.let { match ->
            val fileId = match[1].toInt()
            val row = match[2].toInt()
            val message = match[3]
            GlslError(message, row, fileId)
        } ?: GlslError(line)
    }

    companion object {
        val pattern = Regex("^ERROR: (\\d+):(\\d+): (.*)$")
    }
}

data class GlslError(val message: String, val row: Int = -1, val fileId: Int = -1)