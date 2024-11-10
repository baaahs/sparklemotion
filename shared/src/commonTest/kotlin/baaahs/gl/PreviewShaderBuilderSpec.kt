package baaahs.gl

import baaahs.control.OpenColorPickerControl
import baaahs.control.OpenSliderControl
import baaahs.describe
import baaahs.gadgets.Slider
import baaahs.gl.glsl.GlslError
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.render.PreviewRenderEngine
import baaahs.glsl.Shaders
import baaahs.kotest.value
import baaahs.model.ModelInfo
import baaahs.scene.SceneMonitor
import baaahs.show.Shader
import baaahs.shows.FakeGlContext
import ext.kotlinx_coroutines_test.TestCoroutineDispatcher
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.test.assertNotNull

@InternalCoroutinesApi
class PreviewShaderBuilderSpec : DescribeSpec({
    describe<PreviewShaderBuilder> {
        val shader by value { Shaders.checkerboard }
        val dispatcher by value { TestCoroutineDispatcher() }
        val previewShaderBuilder by value {
            PreviewShaderBuilder(
                shader, testToolchain, SceneMonitor(ModelInfo.EmptyScene),
                coroutineScope = CoroutineScope(dispatcher)
            )
        }
        val renderEngine by value { PreviewRenderEngine(FakeGlContext(), 100, 100) }

        it("is in Unbuilt state") {
            previewShaderBuilder.state.shouldBe(ShaderBuilder.State.Unbuilt)
        }

        context("when startBuilding() is called") {
            beforeEach {
                previewShaderBuilder.startBuilding()
            }

            it("is in Analyzing state") {
                previewShaderBuilder.state.shouldBe(ShaderBuilder.State.Analyzing)
            }

            context("when Analyzing succeeds") {
                beforeEach { dispatcher.runOne() }

                it("is in Linking state") {
                    previewShaderBuilder.state.shouldBe(ShaderBuilder.State.Linking)
                    previewShaderBuilder.openShader.shouldNotBeNull()
                }

                context("after idle") {
                    beforeEach { dispatcher.runCurrent() }

                    it("is in Linked state") {
                        previewShaderBuilder.state.shouldBe(ShaderBuilder.State.Linked)
                    }

                    it("has a previewPatch") {
                        assertNotNull(previewShaderBuilder.previewPatchSet)
                    }

                    it("has a linkedPatch") {
                        assertNotNull(previewShaderBuilder.linkedProgram)
                    }

                    it("has no gadgets yet") {
                        previewShaderBuilder.gadgets.shouldBeEmpty()
                    }

                    context("when there's a problem parsing hints") {
                        override(shader) {
                            Shader(
                                "Shader",
                                /**language=glsl*/
                                """
                                    uniform float foo; // @@something.bad
                                    vec4 main(vec4 inColor) {
                                        return inColor;
                                    }
                                """.trimIndent()
                            )
                        }

                        it("should not result in a build error") {
                            previewShaderBuilder.state.shouldBe(ShaderBuilder.State.Linked)
                            previewShaderBuilder.openShader!!.errors.shouldBeEmpty()
                        }
                    }

                    context("when startCompile() is called") {
                        beforeEach { previewShaderBuilder.startCompile(renderEngine) }

                        it("is in Compiling state") {
                            previewShaderBuilder.state.shouldBe(ShaderBuilder.State.Compiling)
                        }

                        context("after idle") {
                            beforeEach { dispatcher.runCurrent() }

                            it("is in Success state") {
                                previewShaderBuilder.state.shouldBe(ShaderBuilder.State.Success)
                            }

                            it("has gadgets") {
                                previewShaderBuilder.gadgets.map {
                                    when (val openControl = it.openControl) {
                                        is OpenSliderControl -> openControl.slider
                                        is OpenColorPickerControl -> openControl.colorPicker
                                        else -> error("huh? unsupported $openControl")
                                    }
                                }
                                    .shouldContainExactly(Slider("Checkerboard Size", .1f, .001f, 1f))
                            }
                        }
                    }
                }
            }

            context("when Analyzing fails") {
                override(shader) {
                    Shader(
                        "Shader",
                        /**language=glsl*/
                        """
                            #unknownDirective BAIL BAIL BAIL
                            vec4 main(vec4 inColor) {
                                return inColor;
                            }
                        """.trimIndent()
                    )
                }
                beforeEach { dispatcher.runOne() }

                it("should result in a build error") {
                    previewShaderBuilder.state.shouldBe(ShaderBuilder.State.Errors)
                    previewShaderBuilder.openShader!!.errors.shouldContainExactly(
                        GlslError("unknown directive #unknownDirective", row = 1)
                    )
                }
            }
        }
    }
})