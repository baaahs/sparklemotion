package baaahs.app.ui

import baaahs.ui.Icon

actual fun getCommonIcons() = object : PlatformIcons {
    override val Add get() = jsIcon(materialui.icons.AddCircleOutline)
    override val Download get() = jsIcon(materialui.icons.GetApp)
    override val Reload get() = jsIcon(materialui.icons.Cached)

    override val Fixture get() = jsIcon(materialui.icons.Airplay)
    override val Patch get() = jsIcon(materialui.icons.SettingsInputComponent)
    override val DataSource: Icon get() = jsIcon(materialui.icons.Input)
    override val Layout: Icon get() = jsIcon(materialui.icons.Dashboard)
    override val ShaderChannel: Icon get() = jsIcon(materialui.icons.PowerInput)
    override val Settings get() = jsIcon(materialui.icons.Settings)
    override val None: Icon get() = jsIcon(materialui.icons.NotInterested)

    override val ProjectionShader get() = jsIcon(materialui.icons.CastConnected)
    override val DistortionShader get() = jsIcon(materialui.icons.Map)
    override val PaintShader get() = jsIcon(materialui.icons.Image)
    override val FilterShader get() = jsIcon(materialui.icons.FilterBAndW)
    override val MoverShader get() = jsIcon(materialui.icons.OpenWith)
    override val UnknownShader get() = jsIcon(materialui.icons.ShowChart)

    override val BeatLinkControl get() = jsIcon(materialui.icons.AvTimer)
    override val Button get() = jsIcon(materialui.icons.CropLandscape)
    override val ButtonGroup get() = jsIcon(materialui.icons.ViewModule)
    override val ColorPalette get() = jsIcon(materialui.icons.Palette)
    override val ShaderLibrary get() = jsIcon(materialui.icons.PhotoLibrary)
    override val SoundAnalysisControl get() = jsIcon(materialui.icons.Equalizer)
    override val SoundInput get() = jsIcon(materialui.icons.Mic)
    override val Vacuity get() = jsIcon(materialui.icons.CheckBoxOutlineBlank)
    override val Visualizer get() = jsIcon(materialui.icons.Visibility)

    override val Info: Icon get() = jsIcon(materialui.icons.Info)
    override val Warning: Icon get() = jsIcon(materialui.icons.Warning)
    override val Error: Icon get() = jsIcon(materialui.icons.Error)
}

fun jsIcon(materialIcon: materialui.Icon): Icon = JsIcon(materialIcon)

private class JsIcon(private val icon: materialui.Icon) : Icon {
    override fun getReactIcon(): materialui.Icon = icon
}