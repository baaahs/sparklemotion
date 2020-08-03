package baaahs.glshaders

import baaahs.OpenShow
import baaahs.glsl.Shaders
import baaahs.show.*
import baaahs.shows.FakeGlslContext
import baaahs.shows.FakeShowPlayer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object PatchLayeringSpec : Spek({
    describe("Layering of patch links") {
        val autoWirer by value { AutoWirer(Plugins.safe()) }

        fun autoWire(vararg shaders: Shader): PatchEditor {
            return autoWirer.autoWire(*shaders).acceptRoles().resolve()
        }

        val uvShader = Shaders.cylindricalUvMapper.shader
        val blackShader by value {
            Shader("// Black Shader\nvoid main() { gl_FragColor = vec4(0.); }")
        }
        val orangeShader by value {
            Shader("// Orange Shader\nvoid main() { gl_FragColor = vec4(1., .5, 0., 1.); }")
        }
        val brightnessFilter by value {
            Shader("// Brightness Filter\nvoid filterImage(in vec4 colorIn, out vec4 colorOut) { colorOut = colorIn; }")
        }
        val saturationFilter by value {
            Shader("// Saturation Filter\nvoid filterImage(in vec4 colorIn, out vec4 colorOut) { colorOut = colorIn; }")
        }
        val showEditor by value { ShowEditor("test show") }
        val show by value { OpenShow(showEditor.build(ShowBuilder()), FakeShowPlayer(FakeGlslContext())) }

        context("with a show, scene, and patchset patch") {
            beforeEachTest {
                showEditor.apply {
                    addPatch(autoWire(uvShader, blackShader))

                    addScene("scene") {
                        addPatch(autoWire(brightnessFilter))

                        addPatchSet("patchset") {
                            addPatch(autoWire(orangeShader))
                        }
                    }
                }
            }

            it("merges layered patches into a single patch") {
                val mergedPatch = autoWirer.merge(show, show.scenes[0], show.scenes[0].patchSets[0])
                expect(
                    listOf(
                    )
                ) { mergedPatch }
            }
        }
    }
})
