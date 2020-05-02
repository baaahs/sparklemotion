package baaahs.glshaders

import baaahs.Logger
import baaahs.ShowContext
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.glsl.Uniform

class GadgetsPlugin : Plugin {
    override val packageName: String = "baaahs.Gadgets"
    override val name: String = "SparkleMotion Gadgets"

    override fun matchUniformProvider(
        name: String,
        uniformPort: Patch.UniformPort,
        program: GlslProgram,
        showContext: ShowContext
    ): GlslProgram.UniformProvider? {
        val capitalizedName = uniformPort.name.capitalize()
        val varName = uniformPort.varName
        val gadgetId = "glsl_$varName"

        when (uniformPort.type) {
            "float" -> {
                val slider = showContext.getGadget(
                    gadgetId,
                    Slider(
                        capitalizedName //,
                        //                            initialValue = config.getPrimitiveOrNull("initialValue")?.float ?: 1f,
                        //                            minValue = config.getPrimitiveOrNull("minValue")?.float ?: 0f,
                        //                            maxValue = config.getPrimitiveOrNull("maxValue")?.float ?: 1f
                    )
                )
                return object : GlslProgram.UniformProvider {
                    override fun set(uniform: Uniform) = uniform.set(slider.value)
                }
            }

            "vec4" -> {
                val colorPicker = showContext.getGadget(gadgetId, ColorPicker(capitalizedName))
                return object : GlslProgram.UniformProvider {
                    override fun set(uniform: Uniform) {
                        uniform.set(
                            colorPicker.color.redF,
                            colorPicker.color.greenF,
                            colorPicker.color.blueF,
                            colorPicker.color.alphaF
                        )
                    }
                }
            }

            //                "Beat" -> {
            //                    BeatDataSource(showContext.getBeatSource().getBeatData(), showContext.clock)
            //                }
            //                "StartOfMeasure" -> {
            //                    StartOfMeasureDataSource(showContext.getBeatSource().getBeatData(), showContext.clock, binding)
            //                }
            else -> {
                logger.info { "dunno how to handle uniform input type ${uniformPort.type}" }
                return null
            }
        }
    }


    companion object {
        val logger = Logger("GadgetsPlugin")
    }
}