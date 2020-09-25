package baaahs.ui

import baaahs.show.ShaderType
import materialui.Icon
import materialui.icons.Icons as MaterialIcons

object Icons {
    fun forShader(shaderType: ShaderType): Icon {
        return when (shaderType) {
            ShaderType.Projection -> MaterialIcons.CastConnected
            ShaderType.Distortion -> MaterialIcons.Map
            ShaderType.Paint -> MaterialIcons.Image
            ShaderType.Filter -> MaterialIcons.FilterBAndW
        }
    }
}