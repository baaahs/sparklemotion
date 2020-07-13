package baaahs.glshaders

import baaahs.*
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.RadioButtonStrip
import baaahs.gadgets.Slider
import baaahs.glshaders.GlslProgram.DataFeed
import baaahs.glsl.GlslContext.Companion.GL_RGB32F
import baaahs.glsl.GlslRenderer
import baaahs.glsl.Uniform
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import com.danielgergely.kgl.FloatBuffer
import com.danielgergely.kgl.GL_FLOAT
import com.danielgergely.kgl.GL_NEAREST
import com.danielgergely.kgl.GL_RGB
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.float

class CorePlugin : Plugin {
    override val packageName: String = id
    override val title: String = "SparkleMotion Core"

    override fun suggestDataSources(inputPort: InputPort): List<DataSource> {
        val explicit = inputPort.pluginRef?.let {
            val dataSourceBuilder = dataSourceBuildersByName[inputPort.pluginRef.resourceName]
                ?: error("unknown resource \"${inputPort.pluginRef.resourceName}\"")
            dataSourceBuilder.build(inputPort)
        }

        val suggestion = explicit
            ?: inputPort.contentType?.let { supportedContentTypes[it]?.build(inputPort) }

        return suggestion?.let { listOf(it) }
            ?: supportedContentTypes.values.map { it.suggestDataSources(inputPort) }.flatten()
    }

    override fun findDataSource(
        resourceName: String,
        inputPort: InputPort
    ): DataSource? {
        val dataSourceBuilder = dataSourceBuildersByName[resourceName]
            ?: error("unknown plugin resource $resourceName")
        return dataSourceBuilder.build(inputPort)
    }

//    @Serializable
//    class NoOp : DataSource, DataFeed {
//        constructor(inputPortRef: InputPortRef) {
//            this.id = inputPortRef.id
//            this.supportedTypes = listOf(inputPortRef.type)
//        }
//
//        override val id: String
//        override val dataSourceName: String
//            get() =
//        override val supportedTypes: List<String>
//
//        override fun create(showResources: ShowResources): DataFeed = this
//
//        override fun set(uniform: Uniform) {
//            // no-op
//        }
//    }

    @Serializable
    data class Resolution(@Transient val `_`: Boolean = true) : DataSource {
        companion object : DataSourceBuilder<Resolution> {
            override val resourceName: String get() = "Resolution"
            override fun build(inputPort: InputPort): Resolution = Resolution()
        }

        override val dataSourceName: String get() = "Resolution"
        override fun getType(): String = "vec2"

        override fun createFeed(showResources: ShowResources, id: String): DataFeed =
            object : DataFeed, GlslProgram.ResolutionListener, RefCounted by RefCounter() {
                var x = 1f
                var y = 1f

                override fun bind(glslProgram: GlslProgram): GlslProgram.Binding =
                    GlslProgram.SingleUniformBinding(glslProgram, this@Resolution, id, this) { uniform ->
                        uniform.set(x, y)
                    }

                override fun onResolution(x: Float, y: Float) {
                    this.x = x
                    this.y = y
                }
            }
    }

    @Serializable
    data class Time(@Transient val `_`: Boolean = true) : DataSource {
        companion object : DataSourceBuilder<Time> {
            override val resourceName: String get() = "Time"
            override fun build(inputPort: InputPort): Time = Time()
        }

        override val dataSourceName: String get() = "Time"
        override fun getType(): String = "float"

        override fun createFeed(showResources: ShowResources, id: String): DataFeed =
            object : DataFeed, RefCounted by RefCounter() {
                override fun bind(glslProgram: GlslProgram): GlslProgram.Binding =
                    GlslProgram.SingleUniformBinding(glslProgram, this@Time, id, this) { uniform ->
                        val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f
                        uniform.set(thisTime)
                    }
            }
    }

    @Serializable
    data class PixelCoordsTexture(@Transient val `_`: Boolean = true) : DataSource {
        companion object : DataSourceBuilder<PixelCoordsTexture> {
            override val resourceName: String get() = "PixelCoords"
            override fun build(inputPort: InputPort): PixelCoordsTexture = PixelCoordsTexture()
        }

        override val dataSourceName: String get() = "Pixel Coordinates Texture"
        override fun getType(): String = "sampler2D"
        override fun suggestId(): String = "pixelCoordsTexture"

        override fun createFeed(showResources: ShowResources, id: String): DataFeed =
            object : DataFeed, GlslRenderer.ArrangementListener, RefCounted by RefCounter() {
                private val gl = showResources.glslContext
                private val uvCoordTextureUnit = gl.getTextureUnit(PixelCoordsTexture::class)
                private val uvCoordTexture = gl.check { createTexture() }

                override fun onArrangementChange(arrangement: GlslRenderer.Arrangement) {
                    if (arrangement.pixelCoords.isEmpty()) return

                    val pixWidth = arrangement.pixWidth
                    val pixHeight = arrangement.pixHeight
                    val floatBuffer = FloatBuffer(arrangement.pixelCoords)

                    with(uvCoordTextureUnit) {
                        bindTexture(uvCoordTexture)
                        configure(GL_NEAREST, GL_NEAREST)
                        uploadTexture(0, GL_RGB32F, pixWidth, pixHeight, 0, GL_RGB, GL_FLOAT, floatBuffer)
                    }
                }

                override fun bind(glslProgram: GlslProgram): GlslProgram.Binding =
                    GlslProgram.SingleUniformBinding(glslProgram, this@PixelCoordsTexture, id, this) { uniform ->
                        uniform.set(uvCoordTextureUnit)
                    }

                override fun onFullRelease() {
                    gl.check { deleteTexture(uvCoordTexture) }
                }
            }
    }

    @Serializable
    data class ScreenUvCoord(@Transient val `_`: Boolean = true) : DataSource {
        companion object : DataSourceBuilder<ScreenUvCoord> {
            override val resourceName: String get() = "U/V Coordinate"
            override fun build(inputPort: InputPort): ScreenUvCoord = ScreenUvCoord()
        }

        override val dataSourceName: String get() = "U/V Coordinate"
        override fun getType(): String = error("huh?")
        override fun isImplicit(): Boolean = true
        override fun getVarName(id: String): String = "gl_FragCoord"

        override fun getRenderType(): String? = null

        override fun createFeed(showResources: ShowResources, id: String): DataFeed {
            return object : DataFeed, RefCounted by RefCounter() {

                override fun bind(glslProgram: GlslProgram): GlslProgram.Binding {
                    val dataFeed = this
                    return object : GlslProgram.Binding {
                        override val dataFeed: DataFeed
                            get() = dataFeed
                        override val isValid: Boolean get() = true

                        override fun setOnProgram() {
                            // No-op.
                        }
                    }
                }
            }
        }
    }

    @Serializable
    data class ModelInfoDataSource(val structType: String) : DataSource {
        companion object : DataSourceBuilder<ModelInfoDataSource> {
            override val resourceName: String get() = "Model Info"

            // TODO: dataType should be something like "{vec3,vec3}" probably.
            override fun looksValid(inputPort: InputPort): Boolean =
                    inputPort.dataType == "ModelInfo" || inputPort.contentType == ContentType.ModelInfo

            override fun build(inputPort: InputPort): ModelInfoDataSource = ModelInfoDataSource(inputPort.dataType)
        }

        override val dataSourceName: String get() = "Model Info"
        override fun getType(): String = structType
        override fun getRenderType(): String? = null

        override fun createFeed(showResources: ShowResources, id: String): DataFeed {
            return object : DataFeed, RefCounted by RefCounter() {
                private val varPrefix = getVarName(id)
                override fun bind(glslProgram: GlslProgram): GlslProgram.Binding {
                    val dataFeed = this
                    val modelInfo = showResources.modelInfo
                    val center by lazy { modelInfo.center }
                    val extents by lazy { modelInfo.extents }

                    return object : GlslProgram.Binding {
                        val centerUniform = glslProgram.getUniform("${varPrefix}.center")
                        val extentsUniform = glslProgram.getUniform("${varPrefix}.extents")

                        override val dataFeed: DataFeed
                            get() = dataFeed

                        override val isValid: Boolean
                            get() = centerUniform != null && extentsUniform != null

                        override fun setOnProgram() {
                            centerUniform?.set(center)
                            extentsUniform?.set(extents)
                        }
                    }
                }
            }
        }
    }

    interface GadgetDataSource<T : Gadget> : DataSource {
        fun createGadget(): T

        fun set(gadget: T, uniform: Uniform)

        override fun createFeed(showResources: ShowResources, id: String): DataFeed {
            val gadget = createGadget()
            showResources.createdGadget(id, gadget)
            return object : GadgetDataFeed, RefCounted by RefCounter() {
                override val id: String = id
                override val gadget: Gadget = gadget

                override fun bind(glslProgram: GlslProgram): GlslProgram.Binding {
                    return GlslProgram.SingleUniformBinding(glslProgram, this@GadgetDataSource, id, this) { uniform ->
                        this@GadgetDataSource.set(gadget, uniform)
                    }
                }
            }
        }
    }

    interface GadgetDataFeed : DataFeed {
        val id: String
        val gadget: Gadget
    }

    @Serializable
    data class SliderDataSource(
        val title: String,
        val initialValue: Float,
        val minValue: Float,
        val maxValue: Float,
        val stepValue: Float
    ) : GadgetDataSource<Slider> {
        companion object : DataSourceBuilder<SliderDataSource> {
            override val resourceName: String get() = "Slider"

            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.dataType == "float"

            override fun build(inputPort: InputPort): SliderDataSource {
                val config = inputPort.pluginConfig
                return SliderDataSource(
                    inputPort.title,
                    initialValue = config?.get("default")?.float ?: 1f,
                    minValue = config?.get("min")?.float ?: 0f,
                    maxValue = config?.get("max")?.float ?: 1f,
                    stepValue = config?.get("step")?.float ?: .01f
                )
            }
        }

        override val dataSourceName: String get() = "$title $resourceName"
        override fun getType(): String = "float"
        override fun getRenderType(): String? = "Slider"
        override fun suggestId(): String = "$title Slider".camelize()

        override fun createGadget(): Slider =
            Slider(title, initialValue, minValue, maxValue, stepValue)

        override fun set(gadget: Slider, uniform: Uniform) {
            uniform.set(gadget.value)
        }
    }

    @Serializable
    data class XyPad(
        val title: String,
        val varPrefix: String
    ) : DataSource {
        companion object : DataSourceBuilder<XyPad> {
            override val resourceName: String get() = "XyPad"

            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.dataType == "vec2"

            override fun build(inputPort: InputPort): XyPad =
                XyPad(inputPort.title, inputPort.suggestVarName())
        }

        override val dataSourceName: String get() = "XY Pad"
        override fun getType(): String = "vec2"
        override fun suggestId(): String = "$title XY Pad".camelize()

        override fun createFeed(showResources: ShowResources, id: String): DataFeed {
            return object : DataFeed, RefCounted by RefCounter() {
//                val xControl = showResources.useGadget<Slider>("${varPrefix}_x")
//                val yControl = showResources.useGadget<Slider>("${varPrefix}_y")

                override fun bind(glslProgram: GlslProgram): GlslProgram.Binding {
                    val dataFeed = this
                    return object : GlslProgram.Binding {
                        override val dataFeed: DataFeed
                            get() = dataFeed

                        override val isValid: Boolean
                            get() = false

                        override fun setOnProgram() {
                //                            uniform.set(xControl.value, yControl.value)
                        }
                    }
                }
            }
        }
    }

    @Serializable
    data class ColorPickerProvider(
        val title: String,
        val initialValue: Color
    ) : GadgetDataSource<ColorPicker> {
        companion object : DataSourceBuilder<ColorPickerProvider> {
            override val resourceName: String get() = "Color Picker"

            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.dataType == "vec4"

            override fun build(inputPort: InputPort): ColorPickerProvider {
                val default = inputPort.pluginConfig?.get("default")?.primitive?.contentOrNull

                return ColorPickerProvider(
                    inputPort.title,
                    initialValue = default?.let { Color.from(it) } ?: Color.WHITE
                )
            }
        }

        override val dataSourceName: String get() = "$title ${SliderDataSource.resourceName}"
        override fun getType(): String = "vec4"
        override fun getRenderType(): String? = "ColorPicker"
        override fun suggestId(): String = "$title Color Picker".camelize()

        override fun createGadget(): ColorPicker = ColorPicker(title, initialValue)

        override fun set(gadget: ColorPicker, uniform: Uniform) {
            val color = gadget.color
//            when (inputPortRef.type) {
//                "vec3" -> uniform.set(color.redF, color.greenF, color.blueF)
//                "vec4" ->
            uniform.set(color.redF, color.greenF, color.blueF, color.alphaF)
//            }
        }
    }

    @Serializable
    data class RadioButtonStripProvider(
        val title: String,
        val options: List<String>,
        val initialSelectionIndex: Int
    ) : GadgetDataSource<RadioButtonStrip> {
        companion object : DataSourceBuilder<RadioButtonStripProvider> {
            override val resourceName: String get() = "Radio Button Strip"

            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.dataType == "int"

            override fun build(inputPort: InputPort): RadioButtonStripProvider {
                val config = inputPort.pluginConfig

                val initialSelectionIndex = config?.getPrimitive("default")?.int ?: 0

                val options = config
                    ?.getArrayOrNull("options")
                    ?.map { it.primitive.content }
                    ?: error("no options given")

                return RadioButtonStripProvider(
                    inputPort.title,
                    options,
                    initialSelectionIndex
                )
            }
        }

        override val dataSourceName: String get() = resourceName
        override fun getType(): String = "int"

        override fun createGadget(): RadioButtonStrip {
            return RadioButtonStrip(title, options, initialSelectionIndex)
        }

        override fun set(gadget: RadioButtonStrip, uniform: Uniform) {
            TODO("not implemented")
        }
    }

    @Serializable
    data class Scenes(
        val title: String = "Scenes"
    ) : GadgetDataSource<RadioButtonStrip> {
        companion object : DataSourceBuilder<Scenes> {
            override val resourceName: String get() = "SceneList"
            override fun build(inputPort: InputPort): Scenes = Scenes(inputPort.id)
        }

        override val dataSourceName: String get() = "Scene List"
        override fun isImplicit(): Boolean = true
        override fun getType(): String = error("huh?")
        override fun getRenderType(): String? = "SceneList"

        override fun createGadget(): RadioButtonStrip {
            // TODO: this should be a custom gadget for scenes
            return RadioButtonStrip("Scenes", listOf("Scene 1", "Scene 2"), 0)
        }

        override fun set(gadget: RadioButtonStrip, uniform: Uniform) { /* No-pp.*/
        }
    }

    @Serializable
    data class Patches(
        val title: String = "Patches"
    ) : GadgetDataSource<RadioButtonStrip> {
        companion object : DataSourceBuilder<Patches> {
            override val resourceName: String get() = "PatchList"
            override fun build(inputPort: InputPort): Patches = Patches(inputPort.id)

        }

        override val dataSourceName: String get() = "Patch List"
        override fun isImplicit(): Boolean = true
        override fun getType(): String = error("huh?")
        override fun getRenderType(): String? = "PatchList"

        override fun createGadget(): RadioButtonStrip {
            // TODO: this should be a custom gadget for patches
            return RadioButtonStrip(title, listOf("Patch 1", "Patch 2"), 0)
        }

        override fun set(gadget: RadioButtonStrip, uniform: Uniform) { /* No-pp.*/
        }
    }

    @Serializable
    data class ImageSource(val title: String, val dataType: String) : DataSource {
        companion object : DataSourceBuilder<ImageSource> {
            override val resourceName: String get() = "Image"
            override fun looksValid(inputPort: InputPort): Boolean =
                inputPort.dataType == "sampler2D"
            override fun build(inputPort: InputPort): ImageSource = ImageSource(inputPort.title, inputPort.dataType)
        }

        override val dataSourceName: String get() = "Image"
        override fun getType(): String = "sampler2D"
        override fun suggestId(): String = "$title Image".camelize()

        override fun createFeed(showResources: ShowResources, id: String): DataFeed =
            object : DataFeed, RefCounted by RefCounter() {
                override fun bind(glslProgram: GlslProgram): GlslProgram.Binding =
                    GlslProgram.SingleUniformBinding(glslProgram, this@ImageSource, id, this) { uniform ->
                        // no-op
                    }
            }
    }

    companion object {
        val id = "baaahs.Core"

        val supportedContentTypes = mapOf(
            ContentType.PixelCoordinatesTexture to PixelCoordsTexture,
            ContentType.UvCoordinate to ScreenUvCoord,
            ContentType.ModelInfo to ModelInfoDataSource,
//            UvCoordinate,
            ContentType.Mouse to XyPad,
//            XyzCoordinate,
            ContentType.Color to ColorPickerProvider,
            ContentType.Time to Time,
            ContentType.Resolution to Resolution,
            ContentType.Float to SliderDataSource
//            Int,
//            Unknown
        )
        val dataSourceBuildersByName = supportedContentTypes.values.associateBy { it.resourceName }
    }
}