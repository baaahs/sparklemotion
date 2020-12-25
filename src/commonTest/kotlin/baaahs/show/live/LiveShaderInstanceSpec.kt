package baaahs.show.live

import baaahs.Severity
import baaahs.ShowProblem
import baaahs.describe
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.show.ShaderChannel
import baaahs.show.live.LiveShaderInstance.*
import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object LiveShaderInstanceSpec : Spek({
    describe<LiveShaderInstance> {
        val inputPorts by value { listOf<InputPort>() }
        val outputPort by value { OutputPort(ContentType.Color) }
        val links by value { mapOf<String, Link>() }
        val shaderChannel by value { ShaderChannel.Main }
        val instance by value { LiveShaderInstance(FakeOpenShader(inputPorts, outputPort), links, shaderChannel) }

        context(".isFilter") {
            context("with no input port links") {
                it("isn't a filter") { expect(instance.isFilter).toBe(false) }
            }

            context("when an input port's content type matches the return content type") {
                override(inputPorts) { listOf(InputPort("color", ContentType.Color)) }
                override(links) { mapOf("color" to ConstLink("foo", GlslType.Vec4)) }

                it("isn't a filter") { expect(instance.isFilter).toBe(false) }

                context("linked to a shader channel") {
                    override(links) { mapOf("color" to ShaderChannelLink(ShaderChannel.Main)) }

                    context("on the same channel") {
                        it("is a filter") { expect(instance.isFilter).toBe(true) }
                    }

                    context("on a different channel") {
                        override(shaderChannel) { ShaderChannel("other") }
                        it("isn't a filter") { expect(instance.isFilter).toBe(false) }
                    }
                }
            }

            context("when the return content type doesn't match any of the input ports") {
                override(outputPort) { OutputPort(ContentType.XyCoordinate) }
                it("is a filter") {
                    expect(instance.isFilter).toBe(false)
                }
            }
        }

        context(".problems") {
            context("when the output port's content type is unknown") {
                override(outputPort) { OutputPort(ContentType.Unknown) }

                it("fails to validate") {
                    println("problems: ${instance.problems}")
                    expect(instance.problems.map { it.copy(id = "") })
                        .contains(ShowProblem("Result content type is unknown for shader \"${instance.title}\".", severity = Severity.ERROR, id = ""))
                }
            }
        }
    }
})