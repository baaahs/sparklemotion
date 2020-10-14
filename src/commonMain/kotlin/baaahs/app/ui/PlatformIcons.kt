package baaahs.app.ui

import baaahs.show.ShaderType
import baaahs.ui.Icon


@Suppress("PropertyName")
interface PlatformIcons {
    val Add: Icon

    val Fixture: Icon
    val Patch: Icon
    val DataSource: Icon
    val ShaderChannel: Icon
    val Settings: Icon
    val None: Icon

    val DistortionShader: Icon
    val FilterShader: Icon
    val PaintShader: Icon
    val ProjectionShader: Icon

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