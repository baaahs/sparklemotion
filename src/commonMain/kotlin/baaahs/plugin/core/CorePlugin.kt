package baaahs.plugin.core

import baaahs.app.ui.CommonIcons
import baaahs.control.*
import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.MovingHeadDevice
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.dialect.GenericShaderDialect
import baaahs.gl.shader.dialect.IsfShaderDialect
import baaahs.gl.shader.dialect.ShaderToyShaderDialect
import baaahs.gl.shader.type.*
import baaahs.plugin.*
import baaahs.plugin.core.datasource.*
import baaahs.util.Logger

class CorePlugin(private val pluginContext: PluginContext) : OpenServerPlugin, OpenClientPlugin {
    override val packageName: String = id
    override val title: String = "SparkleMotion Core"

    override val contentTypes: List<ContentType> get() =
        ContentType.coreTypes +
                MovingHeadParams.contentType +
                dataSourceBuilders.map { it.contentType } +
                deviceTypes.map { it.resultContentType } +
                deviceTypes.flatMap { it.dataSourceBuilders.map { builder -> builder.contentType } }

    override val dataSourceBuilders get() = Companion.dataSourceBuilders

    override val addControlMenuItems: List<AddControlMenuItem> get() = listOf(
        AddControlMenuItem("New Button…", CommonIcons.Button) { mutableShow ->
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

    override val deviceTypes: List<DeviceType>
        get() = listOf(
            PixelArrayDevice,
            MovingHeadDevice
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

    companion object : Plugin {
        override val id = "baaahs.Core"

        override fun openForServer(pluginContext: PluginContext): OpenServerPlugin =
            CorePlugin(pluginContext)

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin =
            CorePlugin(pluginContext)

        fun openSafe(pluginContext: PluginContext) = CorePlugin(pluginContext)

        private val dataSourceBuilders = listOf(
            ColorPickerDataSource,
            DateDataSource,
            FixtureInfoDataSource,
            ModelInfoDataSource,
            PixelCoordsTextureDataSource,
            PreviewResolutionDataSource,
            RasterCoordinateDataSource,
            ResolutionDataSource,
            SliderDataSource,
            SwitchDataSource,
            TimeDataSource,
            XyPadDataSource
        )

        private val logger = Logger("CorePlugin")
    }
}
