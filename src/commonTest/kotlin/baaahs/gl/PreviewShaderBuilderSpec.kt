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
import baaahs.model.ModelInfo
import baaahs.scene.SceneMonitor
import baaahs.show.Shader
import baaahs.shows.FakeGlContext
import baaahs.toNotEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import ext.kotlinx_coroutines_test.TestCoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import org.spekframework.spek2.Spek
import kotlin.test.assertNotNull

@InternalCoroutinesApi
object PreviewShaderBuilderSpec : Spek({
    describe<PreviewShaderBuilder> {
        val shader by value { Shaders.checkerboard }
        val dispatcher by value { TestCoroutineDispatcher() }
        val previewShaderBuilder by value {
            PreviewShaderBuilder(shader, testToolchain, SceneMonitor(ModelInfo.EmptyScene), CoroutineScope(dispatcher))
        }
        val renderEngine by value { PreviewRenderEngine(FakeGlContext(), 100, 100) }

        it("is in Unbuilt state") {
            expect(previewShaderBuilder.state).toBe(ShaderBuilder.State.Unbuilt)
        }

        context("when startBuilding() is called") {
            beforeEachTest {
                previewShaderBuilder.startBuilding()
            }

            it("is in Analyzing state") {
                expect(previewShaderBuilder.state).toBe(ShaderBuilder.State.Analyzing)
            }

            context("when Analyzing succeeds") {
                beforeEachTest { dispatcher.runOne() }

                it("is in Linking state") {
                    expect(previewShaderBuilder.state).toBe(ShaderBuilder.State.Linking)
                    expect(previewShaderBuilder.openShader).toNotEqual(null)
                }

                context("after idle") {
                    beforeEachTest { dispatcher.runCurrent() }

                    it("is in Linked state") {
                        expect(previewShaderBuilder.state).toBe(ShaderBuilder.State.Linked)
                    }

                    it("has a previewPatch") {
                        assertNotNull(previewShaderBuilder.previewPatchSet)
                    }

                    it("has a linkedPatch") {
                        assertNotNull(previewShaderBuilder.linkedProgram)
                    }

                    it("has no gadgets yet") {
                        expect(previewShaderBuilder.gadgets).isEmpty()
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
                            expect(previewShaderBuilder.state).toBe(ShaderBuilder.State.Linked)
                            expect(previewShaderBuilder.openShader!!.errors).isEmpty()
                        }
                    }

                    context("when startCompile() is called") {
                        beforeEachTest { previewShaderBuilder.startCompile(renderEngine) }

                        it("is in Compiling state") {
                            expect(previewShaderBuilder.state).toBe(ShaderBuilder.State.Compiling)
                        }

                        context("after idle") {
                            beforeEachTest { dispatcher.runCurrent() }

                            it("is in Success state") {
                                expect(previewShaderBuilder.state).toBe(ShaderBuilder.State.Success)
                            }

                            it("has gadgets") {
                                expect(previewShaderBuilder.gadgets.map {
                                    when (val openControl = it.openControl) {
                                        is OpenSliderControl -> openControl.slider
                                        is OpenColorPickerControl -> openControl.colorPicker
                                        else -> error("huh? unsupported $openControl")
                                    }
                                })
                                    .containsExactly(Slider("Checkerboard Size", .1f, .001f, 1f))
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
                beforeEachTest { dispatcher.runOne() }

                it("should result in a build error") {
                    expect(previewShaderBuilder.state).toBe(ShaderBuilder.State.Errors)
                    expect(previewShaderBuilder.openShader!!.errors).containsExactly(
                        GlslError("unknown directive #unknownDirective", row = 1)
                    )
                }
            }
        }
    }
})