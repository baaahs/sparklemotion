package baaahs.gl.glsl

sealed class GlslType constructor(val glslLiteral: String) {
    init {
        @Suppress("LeakingThis")
        types[glslLiteral] = this
    }

    open fun defaultInitializer(): String = "$glslLiteral(0.)"

    private class OtherGlslType(glslLiteral: String) : GlslType(glslLiteral)
    private class Struct(val glslStruct: GlslCode.GlslStruct) : GlslType(glslStruct.name)

    object Float : GlslType("float")
    object Vec2 : GlslType("vec2")
    object Vec3 : GlslType("vec3")
    object Vec4 : GlslType("vec4")
    object Int : GlslType("int")
    object Sampler2D : GlslType("sampler2D")
    object Void : GlslType("void")

    override fun toString(): String {
        return "GlslType($glslLiteral)"
    }

    companion object {
        val types = mutableMapOf<String, GlslType>()
        val structTypes = mutableMapOf<GlslCode.GlslStruct, GlslType>()

        fun from(glsl: String): GlslType {
            return types.getOrPut(glsl) { OtherGlslType(glsl) }
        }

        fun from(glslStruct: GlslCode.GlslStruct): GlslType {
            return structTypes.getOrPut(glslStruct) { Struct(glslStruct) }
        }
    }
}