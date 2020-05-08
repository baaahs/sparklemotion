import baaahs.Color
import baaahs.Model
import baaahs.MovingHead
import baaahs.gadgets.ColorPicker
import baaahs.geom.Vector3F
import baaahs.glshaders.override
import baaahs.glsl.GlslRendererTest
import baaahs.glsl.UvTranslator
import baaahs.shaders.FakeSurface
import baaahs.shows.FakeGlslContext
import baaahs.shows.FakeShowContext
import baaahs.shows.GlslShow
import com.danielgergely.kgl.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.GroupBody
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.meta.*
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object GlslShowSpec : Spek({
    describe<GlslShow> {
        val shaderSrc by value {
            /**language=glsl*/
            "void main() { gl_FragColor = vec4(gl_FragCoord, 0., 1.); }"
        }

        val fakeGlslContext by value { FakeGlslContext() }
        val glslShow by value { GlslShow("test show", shaderSrc, fakeGlslContext) }
        val model by value { TestModel() }
        val showContext by value { FakeShowContext(fakeGlslContext) }
        val showRenderer by value {
            glslShow.createRenderer(model, showContext).apply {
                surfacesChanged(listOf(FakeSurface(100)), emptyList())
            }
        }

        fun render() {
            showRenderer.nextFrame()
            showContext.drawFrame()
        }

        val fakeProgram by value { fakeGlslContext.programs[1] }

        beforeEachTest { render() }

        context("port wiring") {
            it("wires up UV texture stuff") {
                val fragCoordTextureUnit = fakeProgram.getUniform("in_sm_uvCoordsTexture") as Int
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

                val colorPickerGadget by value { showContext.gadgets["glsl_in_color"] as ColorPicker }

                it("wires it up as a color picker") {
                    expect("Color") { colorPickerGadget.name }
                    expect(Color.WHITE) { colorPickerGadget.initialValue }
                }

                it("sets the uniform from the gadget's initial value") {
                    val colorUniform = fakeProgram.getUniform("in_color")
                    expect(arrayListOf(1f, 1f, 1f, 1f)) { colorUniform }
                }

                it("sets the uniform when the gadget value changes") {
                    colorPickerGadget.color = Color.YELLOW

                    render()
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
