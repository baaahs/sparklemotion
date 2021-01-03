package baaahs.plugin.core

import baaahs.TestMovingHead
import baaahs.TestRenderContext
import baaahs.describe
import baaahs.geom.Vector3F
import baaahs.show.live.link
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object FixtureInfoDataSourceSpec : Spek({
    describe<FixtureInfoDataSource> {
        val movingHead by value { TestMovingHead() }

        val shaderText by value {
            """
                struct FixtureInfo {
                    vec3 origin;
                    vec3 heading;
                };
                uniform FixtureInfo fixtureInfo;
        
                void main(void) {
                    gl_FragColor = vec4(fixtureInfo.origin.xy, fixtureInfo.heading.zw);
                }
            """.trimIndent()
        }

        val testRenderContext by value { TestRenderContext(movingHead) }
        val program by value { testRenderContext.createProgram(shaderText, mapOf(
            "fixtureInfo" to FixtureInfoDataSource().link("fixtureInfo")
        )) }

        beforeEachTest {
            testRenderContext.addFixtures()
            testRenderContext.applyProgram(program)
            testRenderContext.renderEngine.draw()
        }

        it("should set uniforms for origin and heading") {
            val glProgram = testRenderContext.gl.findProgram(program.id)

            val originUniform = glProgram.getUniform<List<Float>>("in_fixtureInfo.origin")
            expect(originUniform.asVector3F()).toEqual(movingHead.origin)

            val headingUniform = glProgram.getUniform<List<Float>>("in_fixtureInfo.heading")
            expect(headingUniform.asVector3F()).toEqual(movingHead.heading)
        }
    }
})

fun Iterable<Float>.asVector3F() = with(iterator()) { Vector3F(next(), next(), next()) }