package baaahs.app.ui

import baaahs.show.ShaderType
import baaahs.ui.Icon


@Suppress("PropertyName")
interface PlatformIcons {
    val Fixture: Icon
    val Patch: Icon
    val Settings: Icon

    val DistortionShader: Icon
    val FilterShader: Icon
    val PaintShader: Icon
    val ProjectionShader: Icon

    val Button: Icon
    val ButtonGroup: Icon

    fun forShader(shaderType: ShaderType): Icon {
        return when (shaderType) {
            ShaderType.Projection -> ProjectionShader
            ShaderType.Distortion -> DistortionShader
            ShaderType.Paint -> PaintShader
            ShaderType.Filter -> FilterShader
        }
    }
}

expect fun getCommonIcons(): PlatformIcons

val CommonIcons = getCommonIcons()