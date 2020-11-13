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

    val BeatLinkControl: Icon
    val Button: Icon
    val ButtonGroup: Icon
    val ColorPalette: Icon
    val Visualizer: Icon
}

expect fun getCommonIcons(): PlatformIcons

val CommonIcons = getCommonIcons()