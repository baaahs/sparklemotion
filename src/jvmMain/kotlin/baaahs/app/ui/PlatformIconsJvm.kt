package baaahs.app.ui

import baaahs.ui.Icon

actual fun getCommonIcons(): PlatformIcons = object : PlatformIcons {
    override val Fixture: Icon = FakeIcon
    override val Patch: Icon = FakeIcon
    override val Settings: Icon = FakeIcon
    override val DistortionShader: Icon = FakeIcon
    override val FilterShader: Icon = FakeIcon
    override val PaintShader: Icon = FakeIcon
    override val ProjectionShader: Icon = FakeIcon
    override val Button: Icon = FakeIcon
    override val ButtonGroup = FakeIcon
}

object FakeIcon : Icon