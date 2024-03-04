package baaahs.gl.glsl

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object GlslTypeSpec : Spek({
    describe<GlslType> {
        context("default initializers") {
            it("should be 0 for float") {
                val glslType = GlslType.Float
                expect(glslType.defaultInitializer).toEqual(GlslExpr("0."))
            }

            it("should be 0 for vec2") {
                val glslType = GlslType.Vec2
                expect(glslType.defaultInitializer).toEqual(GlslExpr("vec2(0.)"))
            }

            it("should be 0 for vec3") {
                val glslType = GlslType.Vec3
                expect(glslType.defaultInitializer).toEqual(GlslExpr("vec3(0.)"))
            }

            it("should be 0 for vec4") {
                val glslType = GlslType.Vec4
                expect(glslType.defaultInitializer).toEqual(GlslExpr("vec4(0.)"))
            }

//            it("should be 0 for mat2") {
//                val glslType = GlslType.Mat2
//                expect(glslType.defaultInitializer).toEqual(GlslExpr("mat2(0.)"))
//            }
//
//            it("should be 0 for mat3") {
//                val glslType = GlslType.Mat3
//                expect(glslType.defaultInitializer).toEqual(GlslExpr("mat3(0.)"))
//            }

            it("should be 0 for mat4") {
                val glslType = GlslType.Matrix4
                expect(glslType.defaultInitializer).toEqual(GlslExpr("mat4(0.)"))
            }

            it("should be 0 for int") {
                val glslType = GlslType.Int
                expect(glslType.defaultInitializer).toEqual(GlslExpr("0"))
            }

//            it("should be 0 for ivec2") {
//                val glslType = GlslType.Ivec2
//                expect(glslType.defaultInitializer).toEqual(GlslExpr("ivec2(0)"))
//            }
//
//            it("should be 0 for ivec3") {
//                val glslType = GlslType.Ivec3
//                expect(glslType.defaultInitializer).toEqual(GlslExpr("ivec3(0)"))
//            }
//
//            it("should be 0 for ivec4") {
//                val glslType = GlslType.Ivec4
//                expect(glslType.defaultInitializer).toEqual(GlslExpr("ivec4(0)"))
//            }
        }

        context("structs") {
            it("has a default initializer") {
                val type = GlslType.Struct(
                    "TestStruct",
                    GlslType.Field("someFloat", GlslType.Float),
                    GlslType.Field("someBool", GlslType.Bool)
                )
                expect(type.defaultInitializer.s).toEqual("{ 0., false }")
            }

            it("allows a field's default value to be overridden") {
                val type = GlslType.Struct(
                    "TestStruct",
                    GlslType.Field("someFloat", GlslType.Float, defaultInitializer = GlslExpr("1.")),
                    GlslType.Field("someBool", GlslType.Bool, defaultInitializer = GlslExpr("true"))
                )
                expect(type.defaultInitializer.s).toEqual("{ 1., true }")
            }

            context("convenience constructor overloads") {
                it("has a default initializer") {
                    val type = GlslType.Struct(
                        "TestStruct",
                        "someFloat" to GlslType.Float,
                        "someVec2" to GlslType.Vec2,
                    )
                    expect(type.defaultInitializer.s).toEqual("{ 0., vec2(0.) }")
                }
            }
        }
    }
})