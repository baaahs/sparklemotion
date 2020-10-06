import baaahs.*
import baaahs.fixtures.FixtureManager
import baaahs.gadgets.ColorPicker
import baaahs.geom.Vector3F
import baaahs.gl.GlContext.Companion.GL_RGB32F
import baaahs.gl.override
import baaahs.gl.patch.AutoWirer
import baaahs.gl.render.ModelRenderer
import baaahs.glsl.Shaders
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.model.ModelInfo
import baaahs.model.MovingHead
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.shaders.FakeFixture
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import baaahs.sim.FakeNetwork
import com.danielgergely.kgl.*
import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.json.JsonPrimitive
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.GroupBody
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.meta.*
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

@InternalCoroutinesApi
@Suppress("unused")
object ShowRunnerSpec : Spek({
    describe<ShowRunner> {
        val shaderSrc by value {
            /**language=glsl*/
            "void main() { gl_FragColor = vec4(gl_FragCoord, 0., 1.); }"
        }

        val fakeGlslContext by value { FakeGlContext() }
        val model by value { TestModel() }
        val autoWirer by value { AutoWirer(Plugins.safe()) }
        val fixtures by value { listOf(FakeFixture(100)) }
        val mutableShow by value {
            MutableShow("test show") {
                addPatch(
                    autoWirer.autoWire(Shaders.cylindricalProjection, Shaders.blue)
                        .acceptSymbolicChannelLinks().resolve()
                )
                addButtonGroup(
                    "Panel", "Scenes"
                ) {
                    addButton("test scene") {
                        addButtonGroup(
                            "Panel", "Backdrops"
                        ) {
                            addButton("test patchset") {
                                addPatch(
                                    autoWirer.autoWire(Shader("Untitled", ShaderType.Paint, shaderSrc))
                                        .acceptSymbolicChannelLinks().resolve()
                                )
                            }
                        }
                    }
                }
            }
        }
        val show by value { mutableShow.build(ShowBuilder()) }
        val showState by value {
            ShowState(
                mapOf(
                    "testPatchsetButton" to mapOf("enabled" to JsonPrimitive(true)),
                    "testSceneButton" to mapOf("enabled" to JsonPrimitive(true))
                )
            )
        }
        val testCoroutineContext by value { TestCoroutineContext("Test") }
        val pubSub by value { PubSub.Server(FakeNetwork().link("test").startHttpServer(0), testCoroutineContext) }
        val glslRenderer by value { ModelRenderer(fakeGlslContext, ModelInfo.Empty) }
        val fixtureManager by value { FixtureManager(glslRenderer) }
        val stageManager by value {
            val fs = FakeFs()
            StageManager(
                Plugins.safe(), glslRenderer, pubSub, Storage(fs, Plugins.safe()), fixtureManager, FakeDmxUniverse(),
                MovingHeadManager(fs, pubSub, emptyList()), FakeClock(), model, testCoroutineContext
            )
        }

        val fakeProgram by value { fakeGlslContext.programs[1] }

        val addControls by value { {} }

        beforeEachTest {
            addControls()
            stageManager.switchTo(show, showState)
            fixtureManager.fixturesChanged(fixtures.map { FakeFixtureReceiver(it) {} }, emptyList())
            stageManager.renderAndSendNextFrame()
        }

        context("port wiring") {
            it("wires up UV texture stuff") {
                val pixelCoordsTextureUnit = fakeProgram.getUniform("in_pixelCoordsTexture") as Int
                val textureConfig = fakeGlslContext.getTextureConfig(pixelCoordsTextureUnit)

                expect(100 to 1) { textureConfig.width to textureConfig.height }
                expect(GL_RGB32F) { textureConfig.internalFormat }
                expect(GL_RGB) { textureConfig.format }
                expect(GL_FLOAT) { textureConfig.type }
                expect(GL_NEAREST) { textureConfig.params[GL_TEXTURE_MIN_FILTER] }
                expect(GL_NEAREST) { textureConfig.params[GL_TEXTURE_MAG_FILTER] }
            }

            context("for vec4 uniforms") {
                override(shaderSrc) {
                    /**language=glsl*/
                    """
                    uniform vec4 color;
                    void main() { gl_FragColor = color; }
                    """.trimIndent()
                }

                override(addControls) {
                    {
                        val colorPickerDataSource = CorePlugin.ColorPickerDataSource("Color", Color.WHITE)
                        mutableShow.addControl("Panel", colorPickerDataSource.buildControl())
                    }
                }

                val colorPickerGadget by value {
                    stageManager.useGadget<ColorPicker>("colorColorPickerControl")
                }

                it("wires it up as a color picker") {
                    expect("Color") { colorPickerGadget.title }
                    expect(Color.WHITE) { colorPickerGadget.initialValue }
                }

                it("sets the uniform from the gadget's initial value") {
                    val colorUniform = fakeProgram.getUniform("in_colorColorPicker")
                    expect(arrayListOf(1f, 1f, 1f, 1f)) { colorUniform }
                }

                it("sets the uniform when the gadget value changes") {
                    colorPickerGadget.color = Color.YELLOW

                    stageManager.renderAndSendNextFrame()
                    val colorUniform = fakeProgram.getUniform("in_colorColorPicker")
                    expect(arrayListOf(1f, 1f, 0f, 1f)) { colorUniform }
                }
            }
        }
    }
})

class TestModel : Model<Model.Surface>() {
    override val name: String = "Test Model"
    override val movingHeads: List<MovingHead> = emptyList()
    override val allSurfaces: List<Surface> = emptyList()
    override val geomVertices: List<Vector3F> = emptyList()
}

@Synonym(SynonymType.GROUP)
@Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
inline fun <reified T> GroupBody.describe(skip: Skip = Skip.No, noinline body: Suite.() -> Unit) {
    describe(T::class.toString(), skip, body)
}
