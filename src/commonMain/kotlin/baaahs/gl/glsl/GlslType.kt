package baaahs.gl.glsl

import baaahs.show.mutable.MutableConstPort
import baaahs.show.mutable.MutablePort

sealed class GlslType constructor(
    val glslLiteral: String,
    val defaultInitializer: GlslExpr = GlslExpr("$glslLiteral(0.)")
) {
    init {
        @Suppress("LeakingThis")
        types[glslLiteral] = this
    }

    val mutableDefaultInitializer: MutablePort get() = MutableConstPort(defaultInitializer.s, this)

    private class OtherGlslType(glslLiteral: String) : GlslType(glslLiteral)
    class Struct(
        val name: String,
        val fields: Map<String, GlslType>,
        defaultInitializer: GlslExpr = initializerFor(fields)
    ) : GlslType(name, defaultInitializer) {
        constructor(glslStruct: GlslCode.GlslStruct)
                : this(glslStruct.name, glslStruct.fields)

        constructor(
            name: String,
            vararg fields: Pair<String, GlslType>
        ) : this(name, mapOf(*fields))

        constructor(
            name: String,
            vararg fields: Pair<String, GlslType>,
            defaultInitializer: GlslExpr
        ) : this(name, mapOf(*fields), defaultInitializer)

        fun toGlsl(namespace: GlslCode.Namespace?, publicStructNames: Set<String>): String {
            val buf = StringBuilder()
            buf.append("struct ${namespace?.qualify(name) ?: name} {\n")
            fields.forEach { (name, type) ->
                val typeStr = if (type is Struct) {
                    if (publicStructNames.contains(name)) name else namespace?.qualify(name) ?: name
                } else type.glslLiteral
                buf.append("    $typeStr $name;\n")
            }
            buf.append("};\n\n")

            return buf.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Struct) return false
            if (!super.equals(other)) return false

            if (name != other.name) return false
            if (fields != other.fields) return false

            return true
        }

        override fun hashCode(): kotlin.Int {
            var result = super.hashCode()
            result = 31 * result + name.hashCode()
            result = 31 * result + fields.hashCode()
            return result
        }


        companion object {
            private fun initializerFor(fields: Map<String, GlslType>): GlslExpr =
                StringBuilder().apply {
                    append("{ ")
                    fields.entries.forEachIndexed { index, (_, glslType) ->
                        if (index > 0)
                            append(", ")
                        append(glslType.defaultInitializer.s)
                    }
                    append(" }")
                }.toString().let { GlslExpr(it) }
        }
    }

    object Bool : GlslType("bool", GlslExpr("false"))
    object Float : GlslType("float", GlslExpr("0."))
    object Vec2 : GlslType("vec2")
    object Vec3 : GlslType("vec3")
    object Vec4 : GlslType("vec4")
    object Int : GlslType("int", GlslExpr("0"))
    object Sampler2D : GlslType("sampler2D")
    object Void : GlslType("void")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GlslType) return false

        if (glslLiteral != other.glslLiteral) return false

        return true
    }

    override fun hashCode(): kotlin.Int {
        return glslLiteral.hashCode()
    }

    override fun toString(): String {
        return "GlslType($glslLiteral)"
    }

    companion object {
        val types = mutableMapOf<String, GlslType>()

        fun from(glsl: String): GlslType {
            return types.getOrPut(glsl) { OtherGlslType(glsl) }
        }
    }
}