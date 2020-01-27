package baaahs.glsl

import baaahs.*
import baaahs.Shader
import baaahs.plugins.InputPlugin
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import baaahs.shaders.GlslShader
import baaahs.shows.GlslShow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.expect

class GlslShowTest {
    @Test
    fun testDataFromPlugin() {
        val program = GlslBase.manager.createProgram(
            """
            // SPARKLEMOTION PLUGIN: SoundAnalysis {}
            uniform float lows;
            void main(void) {}
        """.trimIndent()
        )

        val glslShow = object : GlslShow("show") {
            override val program: Program = program
        }

        val x = getPlugin<FakeInputPlugin>()

        val showApi = object : StubShowApi() {
            override fun <T : InputPlugin> getPlugin(pluginType: KClass<T>): T {
                if (pluginType == FakeInputPlugin::class) {
                    return FakeInputPlugin() as T
                }
                return super.getPlugin(pluginType)
            }
        }
        glslShow.createRenderer(Decom2019Model(), showApi)

        val plugin = showApi.getPlugin(FakeInputPlugin::class)

        expect(
            listOf(
                GlslShader.Param(
                    "lows",
                    GlslPlugin.PluginDataSourceProvider("SoundAnalysis"),
                    GlslShader.Param.Type.FLOAT,
                    json { }
                )
            )
        ) { program.params.filter { it.varName == "lows" } }
    }

    inline fun <reified T> getPlugin(): T {
        if (T::class == FakeInputPlugin::class) {
            return FakeInputPlugin() as T
        }
    }

    class FakeInputPlugin() : InputPlugin {
        override val name = "fake"
        override fun createDataSource(config: JsonObject): InputPlugin.DataSource<*> {
            TODO("not implemented")
        }
    }

    open class StubShowApi : ShowApi {
        override val allMovingHeads: List<MovingHead> get() = TODO("not implemented")
        override val allSurfaces: List<Surface> get() = TODO("not implemented")
        override val allUnusedSurfaces: List<Surface> get() = TODO("not implemented")
        override val clock: Clock get() = TODO("not implemented")
        override val currentBeat: Float get() = TODO("not implemented")

        override fun <T : Gadget> getGadget(name: String, gadget: T): T = TODO("not implemented")

        override fun <B : Shader.Buffer> getShaderBuffer(surface: Surface, shader: Shader<B>): B =
            TODO("not implemented")

        override fun getCompositorBuffer(
            surface: Surface,
            bufferA: Shader.Buffer,
            bufferB: Shader.Buffer,
            mode: CompositingMode,
            fade: Float
        ): CompositorShader.Buffer = TODO("not implemented")

        override fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer = TODO("not implemented")

        override fun getBeatSource(): BeatSource = TODO("not implemented")
        override fun <T : InputPlugin> getPlugin(pluginType: KClass<T>): T = TODO("not implemented")

    }
}