package baaahs.plugin

import baaahs.*
import baaahs.app.ui.CommonIcons
import baaahs.fixtures.*
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.RadioButtonStrip
import baaahs.gadgets.Slider
import baaahs.gl.GlContext
import baaahs.gl.data.*
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.*
import baaahs.gl.shader.dialect.GenericShaderDialect
import baaahs.gl.shader.dialect.ShaderToyShaderDialect
import baaahs.gl.shader.type.*
import baaahs.glsl.Uniform
import baaahs.plugin.core.FixtureInfoDataSource
import baaahs.plugin.core.MutableTransitionControl
import baaahs.plugin.core.TransitionControl
import baaahs.show.*
import baaahs.show.mutable.*
import baaahs.util.Logger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.*

class CorePlugin(private val pluginContext: PluginContext) : Plugin {
    override val packageName: String = id
    override val title: String = "SparkleMotion Core"

    override val contentTypes: List<ContentType> get() =
        ContentType.coreTypes +
                dataSourceBuilders.map { it.contentType } +
                deviceTypes.map { it.resultContentType } +
                deviceTypes.flatMap { it.dataSourceBuilders.map { builder -> builder.contentType } }

    override val dataSourceBuilders get() = CorePlugin.dataSourceBuilders

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
            classSerializer(GadgetControl.serializer()),
            classSerializer(ButtonControl.serializer()),
            classSerializer(ButtonGroupControl.serializer()),
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
            ShaderToyShaderDialect
        )

    override val shaderTypes: List<ShaderType>
        get() = listOf(
            ProjectionShader,
            DistortionShader,
            PaintShader,
            FilterShader,
            MoverShader
        )

    /**
     * Sparkle Motion always uses a resolution of (1, 1), except for previews, which
     * use [PreviewResolutionDataSource] instead.
     */
    @Serializable
    @SerialName("baaahs.Core:Resolution")
    data class ResolutionDataSource(@Transient val `_`: Boolean = true) : DataSource {
        companion object : DataSourceBuilder<ResolutionDataSource> {
            override val resourceName: String get() = "Resolution"
            override val contentType: ContentType get() = ContentType.Resolution
            override val serializerRegistrar get() = classSerializer(serializer())

            override fun build(inputPort: InputPort): ResolutionDataSource =
                ResolutionDataSource()
        }

        override val pluginPackage: String get() = id
        override val title: String get() = "Resolution"
        override fun getType(): GlslType = GlslType.Vec2
        override val contentType: ContentType
            get() = ContentType.Resolution

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
            object : Feed, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed =
                        SingleUniformFeed(glslProgram, this@ResolutionDataSource, id) { uniform ->
                            uniform.set(1f, 1f)
                        }
                }

                override fun release() = Unit
            }
    }

    @Serializable
    @SerialName("baaahs.Core:PreviewResolution")
    data class PreviewResolutionDataSource(@Transient val `_`: Boolean = true) : DataSource {
        companion object : DataSourceBuilder<PreviewResolutionDataSource> {
            override val resourceName: String get() = "PreviewResolution"
            override val contentType: ContentType get() = ContentType.PreviewResolution
            override val serializerRegistrar get() = classSerializer(serializer())
            override fun build(inputPort: InputPort): PreviewResolutionDataSource =
                PreviewResolutionDataSource()
        }

        override val pluginPackage: String get() = id
        override val title: String get() = "PreviewResolution"
        override fun getType(): GlslType = GlslType.Vec2
        override val contentType: ContentType
            get() = ContentType.PreviewResolution

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
            object : Feed, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed = object : ProgramFeed, GlslProgram.ResolutionListener {
                        private val uniform = glslProgram.getUniform(getVarName(id))
                        override val isValid: Boolean = uniform != null

                        private var x = 1f
                        private var y = 1f

                        override fun onResolution(x: Float, y: Float) {
                            this.x = x
                            this.y = y
                        }

                        override fun setOnProgram() {
                            uniform?.set(x, y)
                        }
                    }
                }

                override fun release() = Unit
            }
    }

    @Serializable
    @SerialName("baaahs.Core:Time")
    data class TimeDataSource(@Transient val `_`: Boolean = true) : DataSource {
        companion object : DataSourceBuilder<TimeDataSource> {
            override val resourceName: String get() = "Time"
            override val contentType: ContentType get() = ContentType.Time
            override val serializerRegistrar get() = classSerializer(serializer())
            override fun build(inputPort: InputPort): TimeDataSource =
                TimeDataSource()
        }

        override val pluginPackage: String get() = id
        override val title: String get() = "Time"
        override fun getType(): GlslType = GlslType.Float
        override val contentType: ContentType
            get() = ContentType.Time

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
            object : Feed, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed {
                        val clock = showPlayer.toolchain.plugins.pluginContext.clock
                        return SingleUniformFeed(glslProgram, this@TimeDataSource, id) { uniform ->
                            val thisTime = (clock.now() % 10000.0).toFloat()
                            uniform.set(thisTime)
                        }
                    }
                }

                override fun release() = Unit
            }
    }

    @Deprecated("Obsolete, going away soon.")
    @Serializable
    @SerialName("baaahs.Core:PixelCoordsTexture")
    data class PixelCoordsTextureDataSource(@Transient val `_`: Boolean = true) : DataSource {
        companion object : DataSourceBuilder<PixelCoordsTextureDataSource> {
            override val resourceName: String get() = "PixelCoords"
            override val contentType: ContentType get() = ContentType.PixelCoordinatesTexture
            override val serializerRegistrar get() = classSerializer(serializer())
            override fun looksValid(inputPort: InputPort): Boolean = false
            override fun build(inputPort: InputPort): PixelCoordsTextureDataSource =
                PixelCoordsTextureDataSource()
        }

        override val pluginPackage: String get() = id
        override val title: String get() = "Pixel Coordinates Texture"
        override fun getType(): GlslType = GlslType.Sampler2D
        override val contentType: ContentType
            get() = ContentType.PixelCoordinatesTexture
        override fun suggestId(): String = "pixelCoordsTexture"

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
            object : Feed, RefCounted by RefCounter() {
                override fun release() = super.release()
                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed = object : ProgramFeed {
                        override val isValid: Boolean get() = false
                    }
                }
            }
    }

    @Serializable
    @SerialName("baaahs.Core:ModelInfo")
    data class ModelInfoDataSource(@Transient val `_`: Boolean = true) : DataSource {
        companion object : DataSourceBuilder<ModelInfoDataSource> {
            override val resourceName: String get() = "Model Info"
            override val contentType: ContentType get() = ContentType.ModelInfo
            override val serializerRegistrar get() = classSerializer(serializer())
            private val modelInfoType = ContentType.ModelInfo.glslType

            // TODO: dataType should be something like "{vec3,vec3}" probably.
            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.type == modelInfoType || inputPort.contentType == ContentType.ModelInfo

            override fun build(inputPort: InputPort): ModelInfoDataSource =
                ModelInfoDataSource()
        }

        override val pluginPackage: String get() = id
        override val title: String get() = "Model Info"
        override fun getType(): GlslType = modelInfoType
        override val contentType: ContentType
            get() = ContentType.ModelInfo

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
            return object : Feed, RefCounted by RefCounter() {
                private val varPrefix = getVarName(id)

                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed {
                        val modelInfo = showPlayer.modelInfo
                        val center by lazy { modelInfo.center }
                        val extents by lazy { modelInfo.extents }

                        return object : ProgramFeed {
                            override val updateMode: UpdateMode get() = UpdateMode.ONCE
                            val centerUniform = glslProgram.getUniform("${varPrefix}.center")
                            val extentsUniform = glslProgram.getUniform("${varPrefix}.extents")

                            override val isValid: Boolean
                                get() = centerUniform != null && extentsUniform != null

                            override fun setOnProgram() {
                                centerUniform?.set(center)
                                extentsUniform?.set(extents)
                            }
                        }
                    }
                }

                override fun release() = Unit
            }
        }
    }

    @Serializable
    @SerialName("baaahs.Core:RasterCoordinate")
    data class RasterCoordinateDataSource(@Transient val `_`: Boolean = true) : DataSource {
        companion object : DataSourceBuilder<RasterCoordinateDataSource> {
            override val resourceName: String get() = "RasterCoordinate"
            override val contentType: ContentType get() = ContentType.RasterCoordinate
            override val serializerRegistrar get() = classSerializer(serializer())
            override fun build(inputPort: InputPort): RasterCoordinateDataSource =
                RasterCoordinateDataSource()
        }

        override val pluginPackage: String get() = id
        override val title: String get() = "Raster Coordinate"
        override fun getType(): GlslType = GlslType.Vec4
        override val contentType: ContentType
            get() = ContentType.RasterCoordinate

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
            object : Feed, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram) = object : ProgramFeed {}
                }

                override fun release() = Unit
            }

        override fun isImplicit(): Boolean = true
        override fun getVarName(id: String): String = "gl_FragCoord"
    }

    interface GadgetDataSource<T : Gadget> : DataSource {
        @SerialName("title")
        val gadgetTitle: String

        override fun buildControl(): MutableGadgetControl {
            return MutableGadgetControl(createGadget(), this)
        }

        fun createGadget(): T

        fun set(gadget: T, uniform: Uniform)

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
            val gadget = showPlayer.useGadget<T>(this)
                ?: run {
                    logger.debug { "No control gadget registered for datasource $id, creating one. This is probably busted." }
                    createGadget()
                }

            return object : GadgetFeed, RefCounted by RefCounter() {
                override val id: String = id
                override val gadget: Gadget = gadget

                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed {
                        return SingleUniformFeed(glslProgram, this@GadgetDataSource, id) { uniform ->
                            this@GadgetDataSource.set(gadget, uniform)
                        }
                    }
                }

                override fun release() = Unit
            }
        }
    }

    interface GadgetFeed : Feed {
        val id: String
        val gadget: Gadget
    }

    @Serializable
    @SerialName("baaahs.Core:Slider")
    data class SliderDataSource(
        @SerialName("title")
        override val gadgetTitle: String,
        val initialValue: Float,
        val minValue: Float,
        val maxValue: Float,
        val stepValue: Float? = null
    ) : GadgetDataSource<Slider> {
        companion object : DataSourceBuilder<SliderDataSource> {
            override val resourceName: String get() = "Slider"
            override val contentType: ContentType get() = ContentType.Float
            override val serializerRegistrar get() = classSerializer(serializer())

            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.dataTypeIs(GlslType.Float)

            override fun build(inputPort: InputPort): SliderDataSource {
                val config = inputPort.pluginConfig
                return SliderDataSource(
                    inputPort.title,
                    initialValue = config.getFloat("default") ?: 1f,
                    minValue = config.getFloat("min") ?: 0f,
                    maxValue = config.getFloat("max") ?: 1f,
                    stepValue = config.getFloat("step")
                )
            }

            private fun JsonObject?.getFloat(key: String): Float? {
                return try {
                    this?.get(key)?.jsonPrimitive?.float
                } catch (e: NumberFormatException) {
                    logger.debug(e) {
                        "Invalid number for key \"$key\": ${this?.get(key)?.jsonPrimitive?.contentOrNull}"
                    }
                    null
                }
            }
        }

        override val pluginPackage: String get() = id
        override val title: String get() = "$gadgetTitle $resourceName"
        override fun getType(): GlslType = GlslType.Float
        override val contentType: ContentType
            get() = ContentType.Float
        override fun suggestId(): String = "$gadgetTitle Slider".camelize()

        override fun createGadget(): Slider =
            Slider(gadgetTitle, initialValue, minValue, maxValue, stepValue)

        override fun set(gadget: Slider, uniform: Uniform) {
            uniform.set(gadget.value)
        }
    }

    @Serializable
    @SerialName("baaahs.Core:XyPad")
    data class XyPadDataSource(
        @SerialName("title")
        val gadgetTitle: String,
        val varPrefix: String
    ) : DataSource {
        companion object : DataSourceBuilder<XyPadDataSource> {
            override val resourceName: String get() = "XyPad"
            override val contentType: ContentType get() = ContentType.XyCoordinate
            override val serializerRegistrar get() = classSerializer(serializer())

            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.dataTypeIs(GlslType.Vec2)

            override fun build(inputPort: InputPort): XyPadDataSource =
                XyPadDataSource(inputPort.title, inputPort.suggestVarName())
        }

        override val pluginPackage: String get() = id
        override val title: String get() = "XY Pad"
        override fun getType(): GlslType = GlslType.Vec2
        override val contentType: ContentType
            get() = ContentType.XyCoordinate
        override fun suggestId(): String = "$gadgetTitle XY Pad".camelize()

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
            return object : Feed, RefCounted by RefCounter() {
//                val xControl = showPlayer.useGadget<Slider>("${varPrefix}_x")
//                val yControl = showPlayer.useGadget<Slider>("${varPrefix}_y")

                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed {
                        return object : ProgramFeed {
                            override val isValid: Boolean
                                get() = false

                            override fun setOnProgram() {
                                //                            uniform.set(xControl.value, yControl.value)
                            }
                        }
                    }
                }

                override fun release() = Unit
            }
        }
    }

    @Serializable
    @SerialName("baaahs.Core:ColorPicker")
    data class ColorPickerDataSource(
        @SerialName("title")
        override val gadgetTitle: String,
        val initialValue: Color
    ) : GadgetDataSource<ColorPicker> {
        companion object : DataSourceBuilder<ColorPickerDataSource> {
            override val resourceName: String get() = "ColorPicker"
            override val contentType: ContentType get() = ContentType.Color
            override val serializerRegistrar get() = classSerializer(serializer())

            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.dataTypeIs(GlslType.Vec4)

            override fun build(inputPort: InputPort): ColorPickerDataSource {
                val default = inputPort.pluginConfig?.get("default")?.jsonPrimitive?.contentOrNull

                return ColorPickerDataSource(
                    inputPort.title,
                    initialValue = default?.let { Color.from(it) } ?: Color.WHITE
                )
            }
        }

        override val pluginPackage: String get() = id
        override val title: String get() = "$gadgetTitle $resourceName"
        override fun getType(): GlslType = GlslType.Vec4
        override val contentType: ContentType
            get() = ContentType.Color
        override fun suggestId(): String = "$gadgetTitle Color Picker".camelize()

        override fun createGadget(): ColorPicker = ColorPicker(gadgetTitle, initialValue)

        override fun set(gadget: ColorPicker, uniform: Uniform) {
            val color = gadget.color
//            when (inputPortRef.type) {
//                GlslType.Vec3 -> uniform.set(color.redF, color.greenF, color.blueF)
//                GlslType.Vec4 ->
            uniform.set(color.redF, color.greenF, color.blueF, color.alphaF)
//            }
        }
    }

    @Serializable
    @SerialName("baaahs.Core:RadioButtonStrip")
    data class RadioButtonStripDataSource(
        @SerialName("title")
        override val gadgetTitle: String,
        val options: List<String>,
        val initialSelectionIndex: Int
    ) : GadgetDataSource<RadioButtonStrip> {
        companion object : DataSourceBuilder<RadioButtonStripDataSource> {
            override val resourceName: String get() = "Radio Button Strip"
            override val contentType: ContentType get() = ContentType.Int
            override val serializerRegistrar get() = classSerializer(serializer())

            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.dataTypeIs(GlslType.Int)

            override fun build(inputPort: InputPort): RadioButtonStripDataSource {
                val config = inputPort.pluginConfig

                val initialSelectionIndex = config?.getValue("default")?.jsonPrimitive?.int ?: 0

                val options = config
                    ?.let { it["options"]?.jsonArray }
                    ?.map { it.jsonPrimitive.content }
                    ?: error("no options given")

                return RadioButtonStripDataSource(
                    inputPort.title,
                    options,
                    initialSelectionIndex
                )
            }
        }

        override val pluginPackage: String get() = id
        override val title: String get() = resourceName
        override fun getType(): GlslType = GlslType.Int
        override val contentType: ContentType
            get() = ContentType.Int

        override fun createGadget(): RadioButtonStrip {
            return RadioButtonStrip(gadgetTitle, options, initialSelectionIndex)
        }

        override fun set(gadget: RadioButtonStrip, uniform: Uniform) {
            TODO("not implemented")
        }
    }

    @Serializable
    @SerialName("baaahs.Core:Image")
    data class ImageDataSource(val imageTitle: String) : DataSource {
        companion object : DataSourceBuilder<ImageDataSource> {
            override val resourceName: String get() = "Image"
            override val contentType: ContentType get() = ContentType.Color
            override val serializerRegistrar get() = classSerializer(serializer())
            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.dataTypeIs(GlslType.Sampler2D)

            override fun build(inputPort: InputPort): ImageDataSource =
                ImageDataSource(inputPort.title)
        }

        override val pluginPackage: String get() = id
        override val title: String get() = "Image"
        override fun getType(): GlslType = GlslType.Sampler2D
        override val contentType: ContentType get() = ContentType.Color

        override fun suggestId(): String = "$imageTitle Image".camelize()

        override fun createFeed(showPlayer: ShowPlayer, id: String): Feed =
            object : Feed, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                    override fun bind(glslProgram: GlslProgram): ProgramFeed =
                        SingleUniformFeed(glslProgram, this@ImageDataSource, id) {
                            // no-op
                        }
                }

                override fun release() = Unit
            }
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
            SliderDataSource,
            FixtureInfoDataSource,
            RasterCoordinateDataSource
        )

        private val logger = Logger("CorePlugin")
    }
}
