package baaahs.gl.shader

import baaahs.describe
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.gl.testPlugins
import baaahs.toBeSpecified
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object HintedShaderPrototypeSpec : Spek({
    describe<HintedShaderPrototype> {
        val shaderText by value<String> { toBeSpecified() }
        val prototype by value { HintedShaderPrototypeForTest() }
        val plugins by value { testPlugins() }
        val glslCode by value { GlslAnalyzer(plugins).parse(shaderText) }
        val shaderAnalysis by value { prototype.analyze(glslCode, plugins, null) }

        context("determining a method's output ports") {
            context("when there are none") {
                override(shaderText) { "void main() {}" }

                it("should return none") {
                    expect(shaderAnalysis.outputPorts).isEmpty()
                }
            }

            context("when main() returns a value") {
                override(shaderText) { "vec4 main() { return vec4(1.); }" }

                it("should return it") {
                    expect(shaderAnalysis.outputPorts).containsExactly(
                        OutputPort(ContentType.unknown(GlslType.Vec4), id = OutputPort.ReturnValue, dataType = GlslType.Vec4)
                    )
                }

                context("and has a @return hint") {
                    override(shaderText) { "// @return color\nvec4 main() { return vec4(1.); }" }

                    it("should return it") {
                        expect(shaderAnalysis.outputPorts).containsExactly(
                            OutputPort(Color, id = OutputPort.ReturnValue, dataType = GlslType.Vec4)
                        )
                    }
                }
            }

            context("when main() includes out params") {
                override(shaderText) { "void main(out vec4 colorOut) {}" }

                it("should return it") {
                    expect(shaderAnalysis.outputPorts).containsExactly(
                        OutputPort(
                            ContentType.unknown(GlslType.Vec4),
                            id = "colorOut", dataType = GlslType.Vec4, isParam = true)
                    )
                }

                context("and has a @param hint") {
                    override(shaderText) { "// @param colorOut color\nvoid main(out vec4 colorOut) {}" }

                    it("should return it") {
                        expect(shaderAnalysis.outputPorts).containsExactly(
                            OutputPort(Color, id = "colorOut", dataType = GlslType.Vec4, isParam = true)
                        )
                    }
                }

                context("and has a @type hint on the param") {
                    override(shaderText) { "void main(\n    out vec4 colorOut // @type color\n) {}" }

                    it("should return it") {
                        expect(shaderAnalysis.outputPorts).containsExactly(
                            OutputPort(Color, id = "colorOut", dataType = GlslType.Vec4, isParam = true)
                        )
                    }
                }
            }
        }
    }
})

class HintedShaderPrototypeForTest : HintedShaderPrototype("xxx") {
    override val entryPointName: String = "main"
    override val title: String
        get() = TODO("not implemented")
}