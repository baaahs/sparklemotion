package baaahs.gl

import baaahs.describe
import baaahs.gadgets.Slider
import baaahs.gl.patch.AutoWirer
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.render.PreviewRenderEngine
import baaahs.glsl.Shaders
import baaahs.model.ModelInfo
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.shows.FakeGlContext
import ext.TestCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import org.spekframework.spek2.Spek
import kotlin.test.assertNotNull
import kotlin.test.expect

@InternalCoroutinesApi
object PreviewShaderBuilderSpec : Spek({
    describe<PreviewShaderBuilder> {
        val shader by value { Shaders.checkerboard }
        val autoWirer by value { AutoWirer(Plugins.safe()) }
        val testCoroutineContext by value { TestCoroutineContext("global") }
        val previewShaderBuilder by value {
            PreviewShaderBuilder(shader, autoWirer, ModelInfo.Empty, CoroutineScope(testCoroutineContext))
        }
        val renderEngine by value { PreviewRenderEngine(FakeGlContext(), 100, 100) }

        it("is in Unbuilt state") {
            expect(ShaderBuilder.State.Unbuilt) { previewShaderBuilder.state }
        }

        context("when startBuilding() is called") {
            beforeEachTest {
                previewShaderBuilder.startBuilding()
            }

            it("is in Linking state") {
                expect(ShaderBuilder.State.Linking) { previewShaderBuilder.state }
            }

            context("after idle") {
                beforeEachTest { testCoroutineContext.runAll() }

                it("is in Linked state") {
                    expect(ShaderBuilder.State.Linked) { previewShaderBuilder.state }
                }

                it("has a previewPatch") {
                    assertNotNull(previewShaderBuilder.previewPatch)
                }

                it("has a linkedPatch") {
                    assertNotNull(previewShaderBuilder.linkedPatch)
                }

                it("has no gadgets yet") {
                    expect(emptyList()) { previewShaderBuilder.gadgets }
                }

                context("when there's a problem parsing hints") {
                    override(shader) {
                        Shader("Shader", ShaderType.Filter, """
                            uniform float foo; // @@something.bad
                            vec4 mainFilter(vec4 inColor) {
                                return inColor;
                            }
                        """.trimIndent())
                    }

                    it("should report an error right away") {
                        expect(ShaderBuilder.State.Errors) { previewShaderBuilder.state }
                    }
                }

                context("when startCompile() is called") {
                    beforeEachTest { previewShaderBuilder.startCompile(renderEngine) }

                    it("is in Compiling state") {
                        expect(ShaderBuilder.State.Compiling) { previewShaderBuilder.state }
                    }

                    context("after idle") {
                        beforeEachTest { testCoroutineContext.runAll() }

                        it("is in Success state") {
                            expect(ShaderBuilder.State.Success) { previewShaderBuilder.state }
                        }

                        it("has gadgets") {
                            expect(listOf(
                                Slider("Checkerboard Size", .1f, .001f, 1f),
                            )) { previewShaderBuilder.gadgets.map { it.gadget } }
                        }
                    }
                }
            }
        }
    }
})