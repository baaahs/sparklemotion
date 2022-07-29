package baaahs.mapper

import baaahs.Color
import baaahs.shaders.PixelBrainShader
import baaahs.shaders.SolidBrainShader
import baaahs.sm.brain.proto.BrainShader
import baaahs.sm.brain.proto.BrainShaderMessage

object MapperUtil {
    val activeColor = Color(0x07, 0xFF, 0x07)
    val inactiveColor = Color(0x01, 0x00, 0x01)

    fun solidColor(color: Color): BrainShaderMessage {
        val buf = solidColorBuffer(color)
        return BrainShaderMessage(buf.brainShader, buf)
    }

    fun solidColorBuffer(color: Color): BrainShader.Buffer {
        return if (USE_SOLID_SHADERS) {
            val solidShader = SolidBrainShader()
            solidShader.createBuffer(Mapper.maxPixelsPerBrain).apply { this.color = color }
        } else {
            val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.INDEXED_2)
            pixelShader.createBuffer(Mapper.maxPixelsPerBrain).apply {
                palette[0] = Color.BLACK
                palette[1] = color
                setAll(1)
            }
        }
    }

    fun singlePixelOnBuffer(pixelIndex: Int, color: Color = Color.WHITE): PixelBrainShader.Buffer {
        val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.INDEXED_2)
        return pixelShader.createBuffer(Mapper.maxPixelsPerBrain).apply {
            palette[0] = Color.BLACK
            palette[1] = color
            setAll(0)
            set(pixelIndex, 1)
        }
    }
}