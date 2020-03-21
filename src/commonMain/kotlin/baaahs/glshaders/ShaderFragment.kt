package baaahs.glshaders

class ShaderFragment(
    val title: String,
    val uniforms: List<GlslUniform>,
    val functions: List<GlslFunction>
) {
    val entryPoint: GlslFunction get() = functions.first { it.name == "main" }

    data class GlslUniform(val type: String, val name: String, val comments: List<String> = emptyList())

    data class GlslFunction(
        val type: String, val name: String, val params: String, val body: String,
        val comments: List<String> = emptyList()
    )
}