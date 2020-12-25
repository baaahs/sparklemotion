package baaahs.gl.glsl

sealed class GlslType constructor(val glslLiteral: String) {
    init {
        @Suppress("LeakingThis")
        types[glslLiteral] = this
    }

    open fun defaultInitializer(): String = "$glslLiteral(0.)"

    private class OtherGlslType(glslLiteral: String) : GlslType(glslLiteral)
    data class Struct(
        val name: String,
        val fields: Map<String, GlslType>
    ) : GlslType(name) {
        constructor(glslStruct: GlslCode.GlslStruct) : this(glslStruct.name, glslStruct.fields)
    }

    object Float : GlslType("float")
    object Vec2 : GlslType("vec2")
    object Vec3 : GlslType("vec3")
    object Vec4 : GlslType("vec4")
    object Int : GlslType("int")
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