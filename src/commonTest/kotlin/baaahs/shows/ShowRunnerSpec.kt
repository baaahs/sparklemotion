import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.geom.Vector3F
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.Plugins
import baaahs.glshaders.override
import baaahs.glsl.GlslRenderer
import baaahs.glsl.GlslRendererTest
import baaahs.glsl.UvTranslator
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.shaders.FakeSurface
import baaahs.show.*
import baaahs.shows.FakeGlslContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import baaahs.sim.FakeNetwork
import com.danielgergely.kgl.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.GroupBody
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.meta.*
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

@Suppress("unused")
object ShowRunnerSpec : Spek({
    describe<ShowRunner> {
        val shaderSrc by value {
            /**language=glsl*/
            "void main() { gl_FragColor = vec4(gl_FragCoord, 0., 1.); }"
        }

        val fakeGlslContext by value { FakeGlslContext() }
        val model by value { TestModel() }
        val patch by value { AutoWirer(Plugins.safe()).autoWire(shaderSrc) }
        val surfaces by value { listOf(FakeSurface(100)) }
        val patchSet by value {
            PatchSet(
                "test patch set",
                listOf(PatchMapping(patch.links, Surfaces.AllSurfaces)),
                emptyList(),
                emptyMap()
            )
        }
        val show by value {
            val p = patch
            val ipr = p.dataSourceRefs
            println("ipr = ${ipr}")
            Show("test show",
                listOf(
                    Scene("test scene", listOf(patchSet), listOf(), mapOf())
                ),
                dataSources = patch.dataSources,
                shaderFragments = patch.components.mapValues { (_, component) ->
                    component.shaderFragment.src
                }
            )
        }
        val pubSub by value { PubSub.Server(FakeNetwork().link("test").startHttpServer(0)) }
        val showResources by value { ShowManager(Plugins.safe(), fakeGlslContext, pubSub, show) }
        val showRunner by value {
            ShowRunner(
                model,
                show,
                ShowState.forShow(show),
                showResources,
                StubBeatSource(),
                FakeDmxUniverse(),
                MovingHeadManager(FakeFs(), pubSub, emptyList()),
                FakeClock(),
                GlslRenderer(fakeGlslContext, GlslRendererTest.UvTranslatorForTest),
                pubSub
            )
        }

        val fakeProgram by value { fakeGlslContext.programs[1] }

        beforeEachTest {
            showRunner.surfacesChanged(surfaces.map { FakeSurfaceReceiver(it) {} }, emptyList())
            showRunner.nextFrame()
        }

        context("port wiring") {
            it("wires up UV texture stuff") {
                val fragCoordTextureUnit = fakeProgram.getUniform("in_uvCoordsTexture") as Int
                val textureConfig = fakeGlslContext.getTextureConfig(fragCoordTextureUnit)

                expect(200 to 1) { textureConfig.width to textureConfig.height }
                expect(GL_R32F) { textureConfig.internalFormat }
                expect(GL_RED) { textureConfig.format }
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
                    showResources.useGadget<ColorPicker>("color")
                }

                it("wires it up as a color picker") {
                    expect("Color") { colorPickerGadget.title }
                    expect(Color.WHITE) { colorPickerGadget.initialValue }
                }

                it("sets the uniform from the gadget's initial value") {
                    val colorUniform = fakeProgram.getUniform("in_color")
                    expect(arrayListOf(1f, 1f, 1f, 1f)) { colorUniform }
                }

                it("sets the uniform when the gadget value changes") {
                    colorPickerGadget.color = Color.YELLOW

                    showRunner.nextFrame()
                    val colorUniform = fakeProgram.getUniform("in_color")
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
    override val defaultUvTranslator: UvTranslator = GlslRendererTest.UvTranslatorForTest
}

@Synonym(SynonymType.GROUP)
@Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
inline fun <reified T> GroupBody.describe(skip: Skip = Skip.No, noinline body: Suite.() -> Unit) {
    describe(T::class.toString(), skip, body)
}
