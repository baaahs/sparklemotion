package baaahs.plugins

import baaahs.BeatSource
import baaahs.glsl.GlslPlugin
import baaahs.glsl.Program
import com.danielgergely.kgl.Kgl
import kotlinx.serialization.json.JsonObject

class BeatPlugin(val beatSource: BeatSource) : GlslPlugin {
    override val name: String = "Beat"

    override fun createDataSource(config: JsonObject): GlslPlugin.DataSource {
        return object : GlslPlugin.DataSource {
            override fun getValue(): Any = beatSource.getBeatData()
        }
    }

    override fun forProgram(gl: Kgl, program: Program): GlslPlugin.ProgramContext {
        TODO("not implemented")
    }
}