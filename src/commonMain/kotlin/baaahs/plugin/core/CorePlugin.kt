package baaahs.plugin.core

import baaahs.Gadget
import baaahs.app.ui.CommonIcons
import baaahs.control.*
import baaahs.fixtures.DeviceType
import baaahs.fixtures.MovingHeadDevice
import baaahs.fixtures.PixelArrayDevice
import baaahs.gl.data.Feed
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.dialect.GenericShaderDialect
import baaahs.gl.shader.dialect.IsfShaderDialect
import baaahs.gl.shader.dialect.ShaderToyShaderDialect
import baaahs.gl.shader.type.*
import baaahs.plugin.*
import baaahs.plugin.core.datasource.*
import baaahs.util.Logger

class CorePlugin(private val pluginContext: PluginContext) : Plugin {
    override val packageName: String = id
    override val title: String = "SparkleMotion Core"

    override val contentTypes: List<ContentType> get() =
        ContentType.coreTypes +
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
            classSerializer(VisualizerControl.serializer())
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

    interface GadgetFeed : Feed {
        val id: String
        val gadget: Gadget
    }

    companion object : PluginBuilder {
        override val id = "baaahs.Core"

        override fun build(pluginContext: PluginContext) = CorePlugin(pluginContext)

        private val dataSourceBuilders = listOf(
            PixelCoordsTextureDataSource,
            ModelInfoDataSource,
            XyPadDataSource,
            ColorPickerDataSource,
            TimeDataSource,
            ResolutionDataSource,
            PreviewResolutionDataSource,
            SwitchDataSource,
            SliderDataSource,
            FixtureInfoDataSource,
            RasterCoordinateDataSource
        )

        private val logger = Logger("CorePlugin")
    }
}
