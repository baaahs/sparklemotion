package baaahs.gl.glsl

abstract class GlslException(message: String) : Exception(message) {
    abstract val errors: List<GlslError>
}

class LinkException(
    message: String, row: Int? = null
) : GlslException("Shader link error: $message") {
    override val errors = listOf(GlslError(message, row))
}

class AnalysisException(
    message: String, row: Int? = null
) : GlslException("Shader analysis error: $message") {
    override val errors = listOf(GlslError(message, row))
}

class CompilationException(
    errorMessage: String,
    val source: String? = null
) : GlslException("GLSL compilation error: $errorMessage") {
    override val errors =
        errorMessage
            .replace("\u0000", "") // Sometimes Kgl gives us back null chars?
            .split("\n")
            .filter { it.isNotBlank() }
            .map { line ->
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

class ResourceAllocationException(message: String) : Error(message)

data class GlslError(val message: String, val row: Int = NO_LINE, val fileId: Int = -1) {
    constructor(message: String) :
            this(message, NO_LINE)
    constructor(message: String, row: Int? = null, fileId: Int? = null) :
            this(message, row ?: NO_LINE, fileId ?: -1)

    companion object {
        const val NO_LINE = -1
    }
}