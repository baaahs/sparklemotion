package baaahs.gl.glsl

import baaahs.describe
import baaahs.gl.override
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip

object GlslTypeSpec : Spek({
    describe<GlslType> {
        val type by value<GlslType> { toBeSpecified() }

        describe("arrays") {
            override(type) { GlslType.Int.arrayOf(8) }

            it("has valid a GLSL declaration") {
                expect(type.glslLiteral)
                    .toEqual("int[8]")
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
                expect((type as GlslType.Struct).toGlsl(GlslCode.Namespace("pfx"), emptySet()))
                    .toEqual(
                        """
                        struct pfx_FixtureInfo {
                            vec3 origin;
                            vec3 heading;
                            mat4 matrix;
                        };
                    """.trimIndent() + "\n\n"
                    )
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

                it(
                    "#toGlsl generates valid a GLSL declaration",
                    skip = Skip.Yes("Fix position of array marker in declaration.")
                ) {
                    expect((type as GlslType.Struct).toGlsl(GlslCode.Namespace("pfx"), emptySet()))
                        .toEqual(
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