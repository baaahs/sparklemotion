package baaahs.glshaders

import baaahs.shaders.GlslShader

class Patch(
    shaderFragments: Map<String, ShaderFragment>,
    internal val links: List<Link>
) {
    private val components: Map<String, Component>

    private val fromGlobal: List<Link>
    private val toGlobal: List<Link>

    init {
        val fromById = hashMapOf<String?, ArrayList<Link>>()
        val toById = hashMapOf<String?, ArrayList<Link>>()

        links.forEach { link ->
            val (from, to) = link
            fromById.getOrPut(from.shaderId) { arrayListOf() }.add(link)
            toById.getOrPut(to.shaderId) { arrayListOf() }.add(link)
        }

        components = shaderFragments.entries.mapIndexed { i, (shaderId, shaderFragment) ->
            Component(
                i, shaderId, shaderFragment,
                toById[shaderId] ?: emptyList(),
                fromById[shaderId] ?: emptyList()
            )
        }.associateBy { it.shaderId }

        fromGlobal = fromById[null] ?: emptyList()
        toGlobal = toById[null] ?: emptyList()
    }

    val uniformInputs: List<GlslProgram.UniformInput>
        get() = fromGlobal.mapNotNull { (from, _) -> from as? GlslProgram.UniformInput }

    inner class Component(
        val index: Int,
        val shaderId: String,
        val shaderFragment: ShaderFragment,
        val incomingLinks: List<Link>,
        val outgoingLinks: List<Link>
    ) {
        val prefix = "p$index"
        val namespace = GlslCode.Namespace(prefix)
        val portMap: Map<String, () -> String>

        init {
            val tmpPortMap = hashMapOf<String, () -> String>()

            incomingLinks.forEach { (from, to) ->
                if (to is GlslProgram.ShaderPort) {
                    if (from is GlslProgram.ShaderOut) {
                        tmpPortMap[to.portName] =
                            { components[from.shaderId]?.resultVar ?: error("huh? no ${from.shaderId}?") }
                    } else if (from is GlslProgram.UniformInput) {
                        tmpPortMap[to.portName] =
                            { from.varName }
                    }
                }
            }

            tmpPortMap["gl_FragColor"] = { "sm_pixelColor" }
            portMap = tmpPortMap
        }

        val saveResult = outgoingLinks.any { (from, _) -> from is GlslProgram.ShaderOut }
        val resultVar = namespace.internalQualify("result")
        val resolvedPortMap by lazy { portMap.entries.associate { (k, v) -> k to v() } }

        fun appendDeclaratoryLines(buf: StringBuilder) {
            buf.append("// Shader ID: ", shaderId, "; namespace: ", prefix, "\n")
            buf.append("// ", shaderFragment.name, "\n")

            if (saveResult) {
                buf.append("\n")
                buf.append("${shaderFragment.entryPoint.returnType} $resultVar;\n")
            }

            buf.append(shaderFragment.toGlsl(namespace, resolvedPortMap), "\n")
        }

        fun appendMainLines(buf: StringBuilder) {
            buf.append("  ")
            if (saveResult) { buf.append(resultVar, " = ") }
            buf.append(shaderFragment.invocationGlsl(namespace, resolvedPortMap))
            buf.append(";\n")
        }
    }

    fun toGlsl(): String {
        val buf = StringBuilder()
        buf.append("#ifdef GL_ES\n")
        buf.append("precision mediump float;\n")
        buf.append("#endif\n")
        buf.append("\n")
        buf.append("// SparkleMotion generated GLSL\n")
        buf.append("\n")
        buf.append("layout(location = 0) out vec4 sm_pixelColor;\n")
        buf.append("\n")

        uniformInputs.forEach {
            if (!it.isImplicit)
                buf.append("uniform ${it.type} ${it.varName};\n")
        }
        buf.append("\n")

        components.values.forEach { component ->
            component.appendDeclaratoryLines(buf)
        }

        buf.append("\n#line 10001\n")
        buf.append("void main() {\n")
        components.values.forEach { component ->
            component.appendMainLines(buf)
        }
        buf.append("}\n")
        return buf.toString()
    }

    fun compile(): GlslProgram = GlslProgram(GlslShader.renderContext, this)
}
