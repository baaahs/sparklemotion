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
    val Feed: Icon
    val Layout: Icon
    val Stream: Icon
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
    val DeveloperMode: Icon
    val DisplaySettings: Icon
    val EnterFullScreen: Icon
    val ExitFullScreen: Icon
    val ShaderLibrary: Icon
    val SoundAnalysisControl: Icon
    val SoundInput: Icon
    val Vacuity: Icon
    val Visualizer: Icon

    val Info: Icon
    val Warning: Icon
    val Error: Icon

    val MoreHoriz: Icon
    val MoreVert: Icon
}

object PlaceholderIcons : PlatformIcons {
    override val Add: Icon = PlaceholderIcon
    override val Download: Icon = PlaceholderIcon
    override val Upload: Icon = PlaceholderIcon
    override val Reload: Icon = PlaceholderIcon
    override val Fixture: Icon = PlaceholderIcon
    override val Patch: Icon = PlaceholderIcon
    override val Feed: Icon = PlaceholderIcon
    override val Layout: Icon = PlaceholderIcon
    override val Stream: Icon = PlaceholderIcon
    override val Settings: Icon = PlaceholderIcon
    override val None: Icon = PlaceholderIcon
    override val DistortionShader: Icon = PlaceholderIcon
    override val FilterShader: Icon = PlaceholderIcon
    override val PaintShader: Icon = PlaceholderIcon
    override val MoverShader: Icon = PlaceholderIcon
    override val UnknownShader: Icon = PlaceholderIcon
    override val ProjectionShader: Icon = PlaceholderIcon
    override val BeatLinkControl: Icon = PlaceholderIcon
    override val Button: Icon = PlaceholderIcon
    override val ButtonGroup = PlaceholderIcon
    override val ColorPalette: Icon = PlaceholderIcon
    override val DeveloperMode: Icon = PlaceholderIcon
    override val DisplaySettings: Icon = PlaceholderIcon
    override val EnterFullScreen get() = PlaceholderIcon
    override val ExitFullScreen get() = PlaceholderIcon
    override val ShaderLibrary: Icon = PlaceholderIcon
    override val SoundAnalysisControl: Icon = PlaceholderIcon
    override val SoundInput: Icon = PlaceholderIcon
    override val Vacuity: Icon = PlaceholderIcon
    override val Visualizer: Icon = PlaceholderIcon
    override val Info: Icon = PlaceholderIcon
    override val Warning: Icon = PlaceholderIcon
    override val Error: Icon = PlaceholderIcon
    override val MoreHoriz: Icon = PlaceholderIcon
    override val MoreVert: Icon = PlaceholderIcon

    object PlaceholderIcon : Icon
}

expect fun getCommonIcons(): PlatformIcons

val CommonIcons = getCommonIcons()