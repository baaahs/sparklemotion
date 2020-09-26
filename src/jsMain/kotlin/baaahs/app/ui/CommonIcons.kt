package baaahs.app.ui

import materialui.icons.Icons

actual fun getCommonIcons() = object : PlatformIcons {
    override val Fixture = icon(Icons.Airplay)
    override val Patch = icon(Icons.SettingsInputComponent)
    override val Settings = icon(Icons.Settings)

    override val ProjectionShader = icon(Icons.CastConnected)
    override val DistortionShader = icon(Icons.Map)
    override val PaintShader = icon(Icons.Image)
    override val FilterShader = icon(Icons.FilterBAndW)

    fun icon(materialIcon: materialui.Icon) = JsIcon(materialIcon)
}

private class JsIcon(private val icon: materialui.Icon) : baaahs.ui.Icon {
    override fun getReactIcon(): materialui.Icon = icon
}