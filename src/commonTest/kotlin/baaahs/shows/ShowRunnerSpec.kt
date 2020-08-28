import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.geom.Vector3F
import baaahs.gl.GlContext.Companion.GL_RGB32F
import baaahs.gl.override
import baaahs.gl.patch.AutoWirer
import baaahs.gl.render.ModelRenderer
import baaahs.gl.render.ModelRendererTest
import baaahs.glsl.Shaders
import baaahs.glsl.UvTranslator
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.model.ModelInfo
import baaahs.model.MovingHead
import baaahs.plugin.Plugins
import baaahs.shaders.FakeSurface
import baaahs.show.*
import baaahs.show.Shader
import baaahs.show.live.ShowVisitor
import baaahs.show.mutable.BuildContext
import baaahs.show.mutable.MutableShow
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import baaahs.sim.FakeNetwork
import baaahs.util.UniqueIds
import com.danielgergely.kgl.*
import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi
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
        val patch by value {
            AutoWirer(Plugins.safe()).autoWire(
                Shaders.cylindricalProjection, Shader("Untitled", ShaderType.Paint, shaderSrc)
            ).acceptSymbolicChannelLinks().resolve()
        }
        val surfaces by value { listOf(FakeSurface(100)) }
        val show by value {
            MutableShow("test show").apply {
                addScene("test scene") {
                    addPatchSet("test patchset") {
                        addPatch(patch)
                    }
                }
            }.build(BuildContext())
        }
        val testCoroutineContext by value { TestCoroutineContext("Test") }
        val pubSub by value { PubSub.Server(FakeNetwork().link("test").startHttpServer(0), testCoroutineContext) }
        val glslRenderer by value { ModelRenderer(fakeGlslContext, ModelInfo.Empty) }
        val surfaceManager by value { SurfaceManager(glslRenderer) }
        val stageManager by value {
            val fs = FakeFs()
            StageManager(
                Plugins.safe(), glslRenderer, pubSub, Storage(fs, Plugins.safe()), surfaceManager, FakeDmxUniverse(),
                MovingHeadManager(fs, pubSub, emptyList()), FakeClock(), model
            )
        }

        val fakeProgram by value { fakeGlslContext.programs[1] }

        beforeEachTest {
            giveNiceNamesTo(show)
            stageManager.switchTo(show)
            surfaceManager.surfacesChanged(surfaces.map { FakeSurfaceReceiver(it) {} }, emptyList())
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

                val colorPickerGadget by value {
                    stageManager.useGadget<ColorPicker>("colorColorPicker")
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

fun giveNiceNamesTo(show: Show) {
}

class TestModel : Model<Model.Surface>() {
    override val name: String = "Test Model"
    override val movingHeads: List<MovingHead> = emptyList()
    override val allSurfaces: List<Surface> = emptyList()
    override val geomVertices: List<Vector3F> = emptyList()
    override val defaultUvTranslator: UvTranslator = ModelRendererTest.UvTranslatorForTest
}

@Synonym(SynonymType.GROUP)
@Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
inline fun <reified T> GroupBody.describe(skip: Skip = Skip.No, noinline body: Suite.() -> Unit) {
    describe(T::class.toString(), skip, body)
}
