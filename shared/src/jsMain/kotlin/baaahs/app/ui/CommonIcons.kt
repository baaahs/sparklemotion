package baaahs.app.ui

import baaahs.ui.Icon
import mui.icons.material.*

actual fun getCommonIcons() = object : PlatformIcons {
    override val Add get() = jsIcon(AddCircleOutline)
    override val Edit get() = jsIcon(mui.icons.material.Edit)
    override val Delete get() = jsIcon(mui.icons.material.Delete)
    override val Download get() = jsIcon(mui.icons.material.Download)
    override val Upload get() = jsIcon(mui.icons.material.Upload)
    override val Reload get() = jsIcon(Cached)

    override val Fixture get() = jsIcon(Airplay)
    override val Patch get() = jsIcon(SettingsInputComponent)
    override val Feed: Icon get() = jsIcon(Input)
    override val Layout: Icon get() = jsIcon(Dashboard)
    override val Stream: Icon get() = jsIcon(PowerInput)
    override val Settings get() = jsIcon(mui.icons.material.Settings)
    override val None: Icon get() = jsIcon(NotInterested)

    override val ProjectionShader get() = jsIcon(CastConnected)
    override val DistortionShader get() = jsIcon(Map)
    override val PaintShader get() = jsIcon(Image)
    override val FilterShader get() = jsIcon(FilterBAndW)
    override val MoverShader get() = jsIcon(OpenWith)
    override val UnknownShader get() = jsIcon(ShowChart)

    override val BeatLinkControl get() = jsIcon(AvTimer)
    override val Button get() = jsIcon(CropLandscape)
    override val ButtonGroup get() = jsIcon(ViewModule)
    override val ColorPalette get() = jsIcon(Palette)
    override val DeveloperMode get() = jsIcon(mui.icons.material.DeveloperMode)
    override val DisplaySettings get() = jsIcon(mui.icons.material.DisplaySettings)
    override val EnterFullScreen get() = jsIcon(Fullscreen)
    override val ExitFullScreen get() = jsIcon(FullscreenExit)
    override val ShaderLibrary get() = jsIcon(PhotoLibrary)
    override val SoundAnalysisControl get() = jsIcon(Equalizer)
    override val SoundInput get() = jsIcon(Mic)
    override val Vacuity get() = jsIcon(CheckBoxOutlineBlank)
    override val Visualizer get() = jsIcon(Visibility)

    override val Info: Icon get() = jsIcon(mui.icons.material.Info)
    override val Warning: Icon get() = jsIcon(mui.icons.material.Warning)
    override val Error: Icon get() = jsIcon(mui.icons.material.Error)

    override val MoreHoriz: Icon get() = jsIcon(mui.icons.material.MoreHoriz)
    override val MoreVert: Icon get() = jsIcon(mui.icons.material.MoreVert)
}

fun jsIcon(materialIcon: SvgIconComponent): Icon = JsIcon(materialIcon)

class JsIcon(private val icon: SvgIconComponent) : Icon {
    fun getReactIcon(): SvgIconComponent = icon
}

fun Icon.getReactIcon(): SvgIconComponent = (this as JsIcon).getReactIcon()