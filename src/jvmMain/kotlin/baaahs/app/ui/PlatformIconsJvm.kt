package baaahs.app.ui

import baaahs.ui.Icon

actual fun getCommonIcons(): PlatformIcons = object : PlatformIcons {
    override val Add: Icon = FakeIcon
    override val Download: Icon = FakeIcon
    override val Upload: Icon = FakeIcon
    override val Reload: Icon = FakeIcon
    override val Fixture: Icon = FakeIcon
    override val Patch: Icon = FakeIcon
    override val DataSource: Icon = FakeIcon
    override val Layout: Icon = FakeIcon
    override val ShaderChannel: Icon = FakeIcon
    override val Settings: Icon = FakeIcon
    override val None: Icon = FakeIcon
    override val DistortionShader: Icon = FakeIcon
    override val FilterShader: Icon = FakeIcon
    override val PaintShader: Icon = FakeIcon
    override val MoverShader: Icon = FakeIcon
    override val UnknownShader: Icon = FakeIcon
    override val ProjectionShader: Icon = FakeIcon
    override val BeatLinkControl: Icon = FakeIcon
    override val Button: Icon = FakeIcon
    override val ButtonGroup = FakeIcon
    override val ColorPalette: Icon = FakeIcon
    override val ShaderLibrary: Icon = FakeIcon
    override val SoundAnalysisControl: Icon = FakeIcon
    override val SoundInput: Icon = FakeIcon
    override val Vacuity: Icon = FakeIcon
    override val Visualizer: Icon = FakeIcon
    override val Info: Icon = FakeIcon
    override val Warning: Icon = FakeIcon
    override val Error: Icon = FakeIcon
}

object FakeIcon : Icon