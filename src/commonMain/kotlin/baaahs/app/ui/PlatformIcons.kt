package baaahs.app.ui

import baaahs.ui.Icon


@Suppress("PropertyName")
interface PlatformIcons {
    val Add: Icon
    val Download: Icon
    val Upload: Icon
    val Reload: Icon

    val Fixture: Icon
    val Patch: Icon
    val DataSource: Icon
    val Layout: Icon
    val ShaderChannel: Icon
    val Settings: Icon
    val None: Icon

    val ProjectionShader: Icon
    val DistortionShader: Icon
    val FilterShader: Icon
    val PaintShader: Icon
    val MoverShader: Icon
    val UnknownShader: Icon

    val BeatLinkControl: Icon
    val Button: Icon
    val ButtonGroup: Icon
    val ColorPalette: Icon
    val ShaderLibrary: Icon
    val SoundAnalysisControl: Icon
    val SoundInput: Icon
    val Vacuity: Icon
    val Visualizer: Icon

    val Info: Icon
    val Warning: Icon
    val Error: Icon
}

expect fun getCommonIcons(): PlatformIcons

val CommonIcons = getCommonIcons()