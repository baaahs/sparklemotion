package baaahs.app.ui

import baaahs.ui.Icon
import materialui.icons.Icons

actual fun getCommonIcons() = object : PlatformIcons {
    override val Add get() = icon(Icons.AddCircleOutline)
    override val Download get() = icon(Icons.GetApp)

    override val Fixture get() = icon(Icons.Airplay)
    override val Patch get() = icon(Icons.SettingsInputComponent)
    override val DataSource: Icon get() = icon(Icons.Input)
    override val Layout: Icon get() = icon(Icons.Dashboard)
    override val ShaderChannel: Icon get() = icon(Icons.PowerInput)
    override val Settings get() = icon(Icons.Settings)
    override val None: Icon get() = icon(Icons.NotInterested)

    override val ProjectionShader get() = icon(Icons.CastConnected)
    override val DistortionShader get() = icon(Icons.Map)
    override val PaintShader get() = icon(Icons.Image)
    override val FilterShader get() = icon(Icons.FilterBAndW)
    override val MoverShader get() = icon(Icons.OpenWith)
    override val UnknownShader get() = icon(Icons.ShowChart)

    override val BeatLinkControl get() = icon(Icons.AvTimer)
    override val Button get() = icon(Icons.CropLandscape)
    override val ButtonGroup get() = icon(Icons.ViewModule)
    override val ColorPalette get() = icon(Icons.Palette)
    override val Visualizer get() = icon(Icons.Visibility)

    override val Info: Icon get() = icon(Icons.Info)
    override val Warning: Icon get() = icon(Icons.Warning)
    override val Error: Icon get() = icon(Icons.Error)

    fun icon(materialIcon: materialui.Icon) = JsIcon(materialIcon)
}

private class JsIcon(private val icon: materialui.Icon) : baaahs.ui.Icon {
    override fun getReactIcon(): materialui.Icon = icon
}