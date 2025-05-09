package baaahs.gl.glsl

import baaahs.describe
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.toBeSpecified
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

class GlslTypeSpec : DescribeSpec({
    describe<GlslType> {
        val type by value<GlslType> { toBeSpecified() }

        describe("arrays") {
            override(type) { GlslType.Int.arrayOf(8) }

            it("has valid a GLSL declaration") {
                type.glslLiteral
                    .shouldBe("int[8]")
            }
        }

        describe("structs") {
            override(type) {
                GlslType.Struct(
                    "FixtureInfo",
                    "origin" to GlslType.Vec3,
                    "heading" to GlslType.Vec3,
                    "matrix" to GlslType.Matrix4
                )
            }

            it("#toGlsl generates valid a GLSL declaration") {
                (type as GlslType.Struct).toGlsl(GlslCode.Namespace("pfx"), emptySet())
                    .shouldBe(
                        """
                        struct pfx_FixtureInfo {
                            vec3 origin;
                            vec3 heading;
                            mat4 matrix;
                        };
                    """.trimIndent() + "\n\n"
                    )
            }

            context("collectTransitiveStructs") {
                override(type) {
                    GlslType.Struct(
                        "TopLevel",
                        "thingOne" to GlslType.Struct("ThingOne", "floot" to GlslType.Float),
                        "irrelevantValue" to GlslType.Int,
                        "thingTwo" to GlslType.Struct("ThingTwo", "blool" to GlslType.Bool).arrayOf(2)
                    )
                }

                it("returns a list of distinct structs in the order they should be declared") {
                    type.collectTransitiveStructs().map { it.name }
                        .shouldBe(listOf("ThingOne", "ThingTwo", "TopLevel"))
                }
            }

            context("with arrays") {
                override(type) {
                    GlslType.Struct(
                        "FixtureInfo",
                        "origin" to GlslType.Vec3.arrayOf(3),
                        "heading" to GlslType.Vec3,
                        "matrix" to GlslType.Matrix4
                    )
                }

                // TODO: Fix position of array marker in declaration.
                xit("#toGlsl generates valid a GLSL declaration") {
                    (type as GlslType.Struct).toGlsl(GlslCode.Namespace("pfx"), emptySet())
                        .shouldBe(
                            """
                            struct pfx_FixtureInfo {
                                vec3 origin[3];
                                vec3 heading;
                                mat4 matrix;
                            };
                        """.trimIndent() + "\n\n"
                        )
                }
            }
        }
    }
})