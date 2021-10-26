package baaahs.app.ui

import baaahs.ui.Icon

actual fun getCommonIcons() = object : PlatformIcons {
    override val Add get() = icon(materialui.icons.AddCircleOutline)
    override val Download get() = icon(materialui.icons.GetApp)

    override val Fixture get() = icon(materialui.icons.Airplay)
    override val Patch get() = icon(materialui.icons.SettingsInputComponent)
    override val DataSource: Icon get() = icon(materialui.icons.Input)
    override val Layout: Icon get() = icon(materialui.icons.Dashboard)
    override val ShaderChannel: Icon get() = icon(materialui.icons.PowerInput)
    override val Settings get() = icon(materialui.icons.Settings)
    override val None: Icon get() = icon(materialui.icons.NotInterested)

    override val ProjectionShader get() = icon(materialui.icons.CastConnected)
    override val DistortionShader get() = icon(materialui.icons.Map)
    override val PaintShader get() = icon(materialui.icons.Image)
    override val FilterShader get() = icon(materialui.icons.FilterBAndW)
    override val MoverShader get() = icon(materialui.icons.OpenWith)
    override val UnknownShader get() = icon(materialui.icons.ShowChart)

    override val BeatLinkControl get() = icon(materialui.icons.AvTimer)
    override val Button get() = icon(materialui.icons.CropLandscape)
    override val ButtonGroup get() = icon(materialui.icons.ViewModule)
    override val ColorPalette get() = icon(materialui.icons.Palette)
    override val ShaderLibrary get() = icon(materialui.icons.PhotoLibrary)
    override val SoundAnalysisControl get() = icon(materialui.icons.Equalizer)
    override val Vacuity get() = icon(materialui.icons.CheckBoxOutlineBlank)
    override val Visualizer get() = icon(materialui.icons.Visibility)

    override val Info: Icon get() = icon(materialui.icons.Info)
    override val Warning: Icon get() = icon(materialui.icons.Warning)
    override val Error: Icon get() = icon(materialui.icons.Error)

    fun icon(materialIcon: materialui.Icon) = JsIcon(materialIcon)
}

private class JsIcon(private val icon: materialui.Icon) : baaahs.ui.Icon {
    override fun getReactIcon(): materialui.Icon = icon
}