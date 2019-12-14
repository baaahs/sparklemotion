package baaahs.glsl

import baaahs.shaders.GlslShader
import kotlinx.serialization.json.json
import kotlin.test.Test
import kotlin.test.expect

class ProgramTest {
    @Test
    fun testFindAdjustableValues() {
        if (!glslAvailable()) return

        val program = GlslBase.manager.createProgram(
            """
            // SPARKLEMOTION GADGET: Slider {name: "Scale", initialValue: 5.0, minValue: 1.0, maxValue: 10.0}
            uniform float scale;
            void main() {
                gl_FragColor = vec4(0.,0.,0.,0.);
            }
            """.trimIndent())
        expect(
            listOf(
                GlslShader.Param(
                    "scale",
                    GlslPlugin.GadgetDataSourceProvider("Slider"),
                    GlslShader.Param.Type.FLOAT,
                    json { "name" to "Scale"; "initialValue" to 5.0; "minValue" to 1.0; "maxValue" to 10.0 }
                )
            )
        ) { program.params.filter { it.varName == "scale" } }
    }

    @Test
    fun testFindPluginParams() {
        if (!glslAvailable()) return

        val program = GlslBase.manager.createProgram(
            """
            // SPARKLEMOTION PLUGIN: SoundAnalysis {}
            uniform float lows;
            void main() {
                gl_FragColor = vec4(0.,0.,0.,0.);
            }
            """.trimIndent())
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
}