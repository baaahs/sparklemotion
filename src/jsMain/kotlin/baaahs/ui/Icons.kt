package baaahs.ui

import baaahs.show.ShaderType
import materialui.Icon

object Icons {
    fun forShader(shaderType: ShaderType): Icon {
        return when (shaderType) {
            ShaderType.Projection -> materialui.CastConnected
            ShaderType.Distortion -> materialui.Map
            ShaderType.Color -> materialui.Image
            ShaderType.Filter ->materialui.FilterBAndW
        }
    }
}