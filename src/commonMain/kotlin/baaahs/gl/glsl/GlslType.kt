package baaahs.gl.glsl

enum class GlslType(val glslLiteral: String) {
    Float("float"),
    Vec2("vec2"),
    Vec3("vec3"),
    Vec4("vec4"),
    Int("int"),
    sampler2D("sampler2D");

    open fun defaultInitializer(): String = "$glslLiteral(0.)"
}