package baaahs.plugin.core

import baaahs.TestMovingHeadAdapter
import baaahs.TestRenderContext
import baaahs.describe
import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.gl.glsl.GlslProgramImpl
import baaahs.kotest.value
import baaahs.model.MovingHead
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
object FixtureInfoFeedSpec : DescribeSpec({
    describe<FixtureInfoFeed> {
        val movingHead by value {
            MovingHead(
                "test", "Test", baseDmxChannel = 1, adapter = TestMovingHeadAdapter()
            )
        }

        val shaderText by value {
            """
                struct FixtureInfo {
                    vec3 position;
                    vec3 rotation;
                    mat4 transformation;
                };
                uniform FixtureInfo fixtureInfo;
        
                void main(void) {
                    gl_FragColor = vec4(fixtureInfo.position.xy, fixtureInfo.rotation.zw);
                }
            """.trimIndent()
        }

        val testRenderContext by value { TestRenderContext(movingHead) }
        val program by value { testRenderContext.createProgram(shaderText, mapOf(
            "fixtureInfo" to FixtureInfoFeed().link("fixtureInfo")
        )) as GlslProgramImpl }

        beforeEach {
            testRenderContext.addFixtures()
            testRenderContext.applyProgram(program)
            testRenderContext.renderEngine.draw()
        }

        it("should set uniforms for position, rotation, and transformation") {
            val glProgram = testRenderContext.gl.findProgram(program.id)

            val originUniform = glProgram.getUniform<List<Float>>("in_fixtureInfo.position")
            expect(originUniform.asVector3F()).toEqual(movingHead.position)

            val headingUniform = glProgram.getUniform<List<Float>>("in_fixtureInfo.rotation")
            expect(headingUniform.asEulerAngle()).toEqual(movingHead.rotation)

            val transformationUniform = glProgram.getUniform<List<Float>>("in_fixtureInfo.transformation")
            expect(transformationUniform.asMatrix4F()).toEqual(movingHead.transformation)
        }
    }
})

fun Iterable<Float>.asVector3F() =
    with(iterator()) { Vector3F(next(), next(), next()) }
fun Iterable<Float>.asEulerAngle() =
    with(iterator()) { EulerAngle(next().toDouble(), next().toDouble(), next().toDouble()) }
fun Iterable<Float>.asMatrix4F() =
    with(iterator()) { Matrix4F(FloatArray(16) { next() }) }