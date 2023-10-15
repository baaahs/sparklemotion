package baaahs.gl.glsl

import baaahs.describe
import baaahs.gl.override
import baaahs.plugin.core.GlStruct
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip

object GlStructSpec : Spek({
    describe<GlStruct> {
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
                object : GlStruct("FixtureInfo") {
                    var position by field(GlslType.Vec3)
                    var rotation by field(GlslType.Vec3)
                    var transformation by field(GlslType.Matrix4)
                    var name by field(GlslType.Int.arrayOf(8))
                }.toGlslType()
            }

            it("#toGlsl generates valid a GLSL declaration") {
                expect((type as GlslType.Struct).toGlsl(GlslCode.Namespace("pfx"), emptySet()))
                    .toEqual(
                        """
                        struct pfx_FixtureInfo {
                            vec3 position;
                            vec3 rotation;
                            mat4 transformation;
                            int name[8];
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