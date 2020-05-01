import baaahs.Model
import baaahs.MovingHead
import baaahs.geom.Vector3F
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
        val showContext by value { FakeShowContext() }

        context("port wiring") {
            it("wires up UV texture stuff") {
                val renderer = glslShow.createRenderer(model, showContext)
                renderer.surfacesChanged(listOf(FakeSurface(100)), emptyList())
                renderer.nextFrame()

                showContext.drawFrame()

                val program = fakeGlslContext.programs[1]
                val fragCoordVal = program.getUniform("in_sm_uvCoordsTexture")
                val textureInfo = fakeGlslContext.getTexture(fragCoordVal as Int)!!

                expect(200 to 1) { textureInfo.width to textureInfo.height }
                expect(GL_R32F) { textureInfo.internalFormat }
                expect(GL_RED) { textureInfo.format }
                expect(GL_FLOAT) { textureInfo.type }
                expect(GL_NEAREST) { textureInfo.params[GL_TEXTURE_MIN_FILTER] }
                expect(GL_NEAREST) { textureInfo.params[GL_TEXTURE_MAG_FILTER] }

                println("fragCoordVal = ${textureInfo}")
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
