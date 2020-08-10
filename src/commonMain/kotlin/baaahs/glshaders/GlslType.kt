package baaahs.glshaders

enum class GlslType(val glslLiteral: String) {
    Float("float"),
    Vec2("vec2"),
    Vec3("vec3"),
    Vec4("vec4");

    open fun defaultInitializer(): String = "$glslLiteral(0.)"
}