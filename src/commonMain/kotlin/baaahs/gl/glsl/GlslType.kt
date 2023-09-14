package baaahs.gl.glsl

import baaahs.show.mutable.MutableConstPort
import baaahs.show.mutable.MutablePort

sealed class GlslType(
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
        val fields: List<Field>,
        defaultInitializer: GlslExpr = initializerFor(fields),
        val outputRepresentationOverride: ((varName: String) -> String)? = null,
    ) : GlslType(name, defaultInitializer) {
        constructor(glslStruct: GlslCode.GlslStruct)
                : this(glslStruct.name, glslStruct.fields.entries.toFields())

        constructor(
            name: String,
            vararg fields: Field
        ) : this(name, listOf(*fields))

        constructor(
            name: String,
            vararg fields: Pair<String, GlslType>
        ) : this(name, mapOf(*fields).entries.toFields())

        constructor(
            name: String,
            vararg fields: Pair<String, GlslType>,
            defaultInitializer: GlslExpr,
            outputOverride: ((varName: String) -> String)?
        ) : this(name, mapOf(*fields).entries.toFields(), defaultInitializer = defaultInitializer, outputRepresentationOverride = outputOverride)

        fun toGlsl(namespace: GlslCode.Namespace?, publicStructNames: Set<String>): String {
            val buf = StringBuilder()
            buf.append("struct ${namespace?.qualify(name) ?: name} {\n")
            fields.forEach { field ->
                field.toGlsl(namespace, publicStructNames, buf)
            }
            buf.append("};\n\n")

            return buf.toString()
        }

        override fun matches(otherType: GlslType): Boolean {
            return otherType is Struct &&
                    name == otherType.name &&
                    isSubsetOf(otherType)
        }

        fun isSubsetOf(otherStruct: Struct): Boolean =
            fields.all { otherStruct.fields.contains(it) }

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
            private fun initializerFor(fields: List<Field>): GlslExpr =
                StringBuilder().apply {
                    append("{ ")
                    fields.forEachIndexed { index, field ->
                        if (index > 0)
                            append(", ")
                        append(field.type.defaultInitializer.s)
                    }
                    append(" }")
                }.toString().let { GlslExpr(it) }
        }

        override fun outputRepresentationGlsl(varName: String): String{
            if (outputRepresentationOverride != null) {
                return outputRepresentationOverride!!(varName);
            }
            return buildString {
                append(glslLiteral, "(")
                fields.forEachIndexed { index, field ->
                    if (index > 0) append(",")
                    append("\n        $varName.${field.name}")
                }
                append("\n    )")
            }
        }
    }

    class Field(
        val name: String,
        val type: GlslType,
        val description: String? = null,
        val deprecated: Boolean = false,
    ) {
        fun toGlsl(
            namespace: GlslCode.Namespace?,
            publicStructNames: Set<String>,
            buf: StringBuilder
        ) {
            buf.append("    ")

            fun String.maybeQualify() =
                if (publicStructNames.contains(this)) this
                else namespace?.qualify(this)
                    ?: this

            when (type) {
                is Struct -> {
                    buf.append(type.name.maybeQualify())
                    buf.append(" ")
                    buf.append(name)
                }

                is Array -> {
                    buf.append(type.memberType.glslLiteral.maybeQualify())
                    buf.append(" ")
                    buf.append(name)
                    buf.append("[")
                    buf.append(type.length.toString())
                    buf.append("]")
                }

                else -> {
                    buf.append(type.glslLiteral)
                    buf.append(" ")
                    buf.append(name)
                }
            }

            buf.append(";")

            val comment = if (deprecated) " // Deprecated. $description" else description ?: ""
            buf.append(comment)
            buf.append("\n")
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Field) return false

            if (name != other.name) return false
            if (type != other.type) return false

            return true
        }

        override fun hashCode(): kotlin.Int {
            var result = name.hashCode()
            result = 31 * result + type.hashCode()
            return result
        }
    }

    object Bool : GlslType("bool", GlslExpr("false"))
    object Float : GlslType("float", GlslExpr("0."))
    object Matrix4 : GlslType("mat4")
    object Vec2 : GlslType("vec2")
    object Vec3 : GlslType("vec3")
    object Vec4 : GlslType("vec4")
    object Int : GlslType("int", GlslExpr("0"))
    object Sampler2D : GlslType("sampler2D")
    object Void : GlslType("void")

    class Array(val memberType: GlslType, val length: kotlin.Int) : GlslType("${memberType.glslLiteral}[$length]")

    fun arrayOf(count: kotlin.Int): Array = Array(this, count)

    open fun matches(otherType: GlslType): Boolean =
        this == otherType

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

        fun kotlin.Array<Pair<String, GlslType>>.toFields() =
            map { (name, type) -> Field(name, type) }

        fun Collection<Map.Entry<String, GlslType>>.toFields() =
            map { (name, type) -> Field(name, type) }
    }

     open fun outputRepresentationGlsl(varName: String): String {
        return varName;
    }
}