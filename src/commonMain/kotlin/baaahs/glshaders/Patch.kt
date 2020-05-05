package baaahs.glshaders

import baaahs.glsl.GlslContext

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

    val uniformPorts: List<UniformPortRef>
        get() = fromGlobal.map { it.from }.filterIsInstance<UniformPortRef>()

    inner class Component(
        val index: Int,
        val shaderId: String,
        private val shaderFragment: ShaderFragment,
        incomingLinks: List<Link>,
        outgoingLinks: List<Link>
    ) {
        private val prefix = "p$index"
        private val namespace = GlslCode.Namespace(prefix)
        private val portMap: Map<String, () -> String>

        init {
            val tmpPortMap = hashMapOf<String, () -> String>()

            incomingLinks.forEach { (from, to) ->
                if (to is ShaderPortRef) {
                    if (from is ShaderOut) {
                        tmpPortMap[to.portName] =
                            { components[from.shaderId]?.resultVar ?: error("huh? no ${from.shaderId}?") }
                    } else if (from is UniformPortRef) {
                        tmpPortMap[to.portName] =
                            { from.varName }
                    }
                }
            }

            tmpPortMap["gl_FragColor"] = { "sm_pixelColor" }
            portMap = tmpPortMap
        }

        val saveResult = outgoingLinks.any { (from, _) -> from is ShaderOut }
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

        uniformPorts.forEach {
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

    fun compile(glslContext: GlslContext): GlslProgram =
        GlslProgram(glslContext, this)

    interface PortRef {
        val shaderId: String?

        infix fun linkTo(other: PortRef): Link = Link(this, other)
    }

    data class ShaderOut(override val shaderId: String) : PortRef

    object PixelColor : PortRef {
        override val shaderId: String? = null
    }

    open class UniformPortRef(
        val type: String,
        val name: String,
        val pluginId: String? = null,
        val pluginConfig: Map<String, String> = emptyMap()
    ) : PortRef {
        override val shaderId: String? = null

        open val varName: String get() = "in_$name"
        open val isImplicit = false

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is UniformPortRef) return false

            if (type != other.type) return false
            if (name != other.name) return false
            if (pluginId != other.pluginId) return false
            if (pluginConfig != other.pluginConfig) return false
            if (shaderId != other.shaderId) return false
            if (isImplicit != other.isImplicit) return false

            return true
        }

        override fun hashCode(): Int {
            var result = type.hashCode()
            result = 31 * result + name.hashCode()
            result = 31 * result + (pluginId?.hashCode() ?: 0)
            result = 31 * result + pluginConfig.hashCode()
            result = 31 * result + (shaderId?.hashCode() ?: 0)
            result = 31 * result + isImplicit.hashCode()
            return result
        }

        override fun toString(): String {
            return "UniformPortRef(type='$type', name='$name', pluginId=$pluginId, pluginConfig=$pluginConfig, shaderId=$shaderId, isImplicit=$isImplicit)"
        }
    }

    data class ShaderPortRef(override val shaderId: String, val portName: String) : PortRef

    data class Link(val from: PortRef, val to: PortRef)
}
