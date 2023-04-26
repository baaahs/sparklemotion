package baaahs.plugin.core

import baaahs.app.ui.CommonIcons
import baaahs.control.*
import baaahs.controller.ControllerManager
import baaahs.controller.SacnManager
import baaahs.device.FixtureType
import baaahs.device.MovingHeadDevice
import baaahs.device.PixelArrayDevice
import baaahs.device.ProjectorDevice
import baaahs.dmx.DmxManager
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.dialect.GenericShaderDialect
import baaahs.gl.shader.dialect.IsfShaderDialect
import baaahs.gl.shader.dialect.ShaderToyShaderDialect
import baaahs.gl.shader.type.*
import baaahs.plugin.*
import baaahs.plugin.core.feed.*
import baaahs.sm.brain.BrainManager
import baaahs.util.Logger
import kotlinx.cli.ArgParser

class CorePlugin(
    private val pluginContext: PluginContext
) : OpenServerPlugin, OpenClientPlugin {
    override val packageName: String = id
    override val title: String = "SparkleMotion Core"

    override val contentTypes: List<ContentType> get() =
        ContentType.coreTypes +
                MovingHeadParams.contentType +
                feedBuilders.map { it.contentType } +
                fixtureTypes.map { it.resultContentType } +
                fixtureTypes.flatMap { it.feedBuilders.map { builder -> builder.contentType } }

    override val feedBuilders get() = Companion.feedBuilders

    override val addControlMenuItems: List<AddControlMenuItem> get() = listOf(
        AddControlMenuItem("New Button…", CommonIcons.Button, true) { mutableShow ->
            MutableButtonControl(ButtonControl("New Button"), mutableShow)
        },

        AddControlMenuItem("New Button Group…", CommonIcons.ButtonGroup) { mutableShow ->
            MutableButtonGroupControl(
                "New Button Group",
                ButtonGroupControl.Direction.Horizontal,
                mutableShow = mutableShow
            )
        },

        AddControlMenuItem("New Color Palette…", CommonIcons.ColorPalette) { mutableShow ->
            TODO("not implemented")
        },

        AddControlMenuItem("New Transition Panel…", CommonIcons.Add) { mutableShow ->
            MutableTransitionControl()
        },

        AddControlMenuItem("New Vacuity…", CommonIcons.Visualizer) { mutableShow ->
            MutableVacuityControl("Vacuity")
        },

        AddControlMenuItem("New Visualizer…", CommonIcons.Visualizer) { mutableShow ->
            MutableVisualizerControl()
        }
    )

    override val controlSerializers
        get() = listOf(
            classSerializer(ColorPickerControl.serializer()),
            classSerializer(ButtonControl.serializer()),
            classSerializer(ButtonGroupControl.serializer()),
            classSerializer(SliderControl.serializer()),
            classSerializer(TransitionControl.serializer()),
            classSerializer(VacuityControl.serializer()),
            classSerializer(VisualizerControl.serializer()),
            classSerializer(XyPadControl.serializer())
        )

    override val controllerManagers: List<ControllerManager.Meta>
        get() = listOf(
            BrainManager,
            DmxManager,
            SacnManager
        )

    override val fixtureTypes: List<FixtureType>
        get() = listOf(
            PixelArrayDevice,
            MovingHeadDevice,
            ProjectorDevice
        )

    override val shaderDialects
        get() = listOf(
            GenericShaderDialect,
            ShaderToyShaderDialect,
            IsfShaderDialect
        )

    override val shaderTypes: List<ShaderType>
        get() = listOf(
            ProjectionShader,
            DistortionShader,
            PaintShader,
            FilterShader,
            MoverShader
        )

    companion object : Plugin<Any> {
        override val id = "baaahs.Core"

        override fun getArgs(parser: ArgParser): Any = Any()

        override fun openForServer(pluginContext: PluginContext, args: Any): OpenServerPlugin =
            CorePlugin(pluginContext)

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin =
            CorePlugin(pluginContext)

        fun openSafe(pluginContext: PluginContext) = CorePlugin(pluginContext)

        private val feedBuilders = listOf(
            ColorPickerFeed,
            DateFeed,
            FixtureInfoFeed,
            ImageFeed,
            ModelInfoFeed,
            PixelCoordsTextureFeed,
            PreviewResolutionFeed,
            RasterCoordinateFeed,
            ResolutionFeed,
            SliderFeed,
            SwitchFeed,
            TimeFeed,
            XyPadFeed
        )

        private val logger = Logger("CorePlugin")
    }
}
