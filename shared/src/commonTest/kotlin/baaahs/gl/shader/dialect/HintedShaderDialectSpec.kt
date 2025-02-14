package baaahs.gl.shader.dialect

import baaahs.describe
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslParser
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.gl.patch.ContentType.Companion.Float
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.gl.testPlugins
import baaahs.kotest.value
import baaahs.only
import baaahs.plugin.PluginRef
import baaahs.plugin.Plugins
import baaahs.toBeSpecified
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.serialization.json.buildJsonObject

@Suppress("unused")
class HintedShaderDialectSpec : DescribeSpec({
    describe<HintedShaderAnalyzer> {
        val shaderText by value<String> { toBeSpecified() }
        val plugins by value { testPlugins() }
        val glslCode by value { GlslParser().parse(shaderText) }
        val analyzer by value { HintedShaderAnalyzerForTest(glslCode, plugins) }
        val shaderAnalysis by value { analyzer.analyze(null) }

        context("determining a method's output ports") {
            context("when there are none") {
                override(shaderText) { "void main() {}" }

                it("should return none") {
                    shaderAnalysis.outputPorts.shouldBeEmpty()
                }
            }

            context("when main() returns a value") {
                override(shaderText) { "vec4 main() { return vec4(1.); }" }

                it("should return it") {
                    shaderAnalysis.outputPorts.shouldContainExactly(
                        OutputPort(
                            ContentType.unknown(GlslType.Vec4),
                            id = OutputPort.ReturnValue,
                            dataType = GlslType.Vec4
                        )
                    )
                }

                context("and has a @return hint") {
                    override(shaderText) { "// @return color\nvec4 main() { return vec4(1.); }" }

                    it("should return it") {
                        shaderAnalysis.outputPorts.shouldContainExactly(
                            OutputPort(Color, id = OutputPort.ReturnValue, dataType = GlslType.Vec4)
                        )
                    }
                }
            }

            context("when main() includes out params") {
                override(shaderText) { "void main(out vec4 colorOut) {}" }

                it("should return it") {
                    shaderAnalysis.outputPorts.shouldContainExactly(
                        OutputPort(
                            ContentType.unknown(GlslType.Vec4),
                            id = "colorOut", dataType = GlslType.Vec4, isParam = true
                        )
                    )
                }

                context("and has a @param hint") {
                    override(shaderText) { "// @param colorOut color\nvoid main(out vec4 colorOut) {}" }

                    it("should return it") {
                        shaderAnalysis.outputPorts.shouldContainExactly(
                            OutputPort(Color, id = "colorOut", dataType = GlslType.Vec4, isParam = true)
                        )
                    }
                }

                context("and has a @type hint on the param") {
                    override(shaderText) { "void main(\n    out vec4 colorOut // @type color\n) {}" }

                    it("should return it") {
                        shaderAnalysis.outputPorts.shouldContainExactly(
                            OutputPort(Color, id = "colorOut", dataType = GlslType.Vec4, isParam = true)
                        )
                    }
                }
            }
        }

        context("determining a method's input ports") {
            context("when an in param specifies a plugin feed") {
                override(shaderText) { "void main(\n  float arg // @@Slider\n) {}" }
                val inputPort by value { shaderAnalysis.inputPorts.only("input port") }

                it("should use the feed's content type") {
                    inputPort.copy(glslArgSite = null)
                        .shouldBe(
                            InputPort(
                                "arg", Float,
                                pluginRef = PluginRef("baaahs.Core", "Slider"),
                                pluginConfig = buildJsonObject {}
                            )
                        )
                }
            }
        }
    }
})

class HintedShaderAnalyzerForTest(
    glslCode: GlslCode,
    plugins: Plugins
) : HintedShaderAnalyzer(glslCode, plugins) {
    override val dialect: ShaderDialect
        get() = GenericShaderDialect
    override val entryPointName: String = "main"
}