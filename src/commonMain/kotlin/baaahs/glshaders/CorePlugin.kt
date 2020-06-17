package baaahs.glshaders

import baaahs.Color
import baaahs.ShowResources
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.RadioButtonStrip
import baaahs.gadgets.Slider
import baaahs.getTimeMillis
import baaahs.glshaders.GlslProgram.DataFeed
import baaahs.glsl.GlslRenderer
import baaahs.glsl.Uniform
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import com.danielgergely.kgl.*
import kotlinx.serialization.Serializable
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
//        return when (resourceName) {
//            "resolution" -> Resolution(inputPort.id)
//            "time" -> Time(inputPort.id)
//            "uvCoords" -> UvCoord(inputPort.id)
//
//            "xyCoord" -> XyPad(inputPort)
//
//            "Slider" -> SliderProvider(inputPort)
//            "ColorPicker" -> ColorPickerProvider(inputPort)
//            "RadioButtonStrip" -> RadioButtonStripProvider(inputPort)
//
//            "Scenes" -> Scenes(inputPort)
//            "Patches" -> Patches(inputPort)
//
//            "none" -> NoOp(inputPort)
//            "invalid" -> error("no provider for $inputPort")
//
//            else -> throw IllegalArgumentException("unknown type $resourceName")
//        }
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
    data class Resolution(override val id: String) : DataSource {
        companion object : DataSourceBuilder<Resolution> {
            override val resourceName: String get() = "Resolution"
            override fun suggestDataSources(inputPort: InputPort): List<Resolution> = emptyList()
            override fun build(inputPort: InputPort): Resolution = Resolution(inputPort.id)
        }

        override val dataSourceName: String get() = "Resolution"
        override fun getType(): String = "vec2"

        override fun create(showResources: ShowResources): DataFeed =
            object : DataFeed, GlslProgram.ResolutionListener {
                var x = 1f
                var y = 1f

                override fun set(uniform: Uniform) = uniform.set(x, y)

                override fun onResolution(x: Float, y: Float) {
                    this.x = x
                    this.y = y
                }
            }
    }

    @Serializable
    data class Time(override val id: String) : DataSource {
        companion object : DataSourceBuilder<Time> {
            override val resourceName: String get() = "Time"
            override fun suggestDataSources(inputPort: InputPort): List<Time> = emptyList()
            override fun build(inputPort: InputPort): Time = Time(inputPort.id)
        }

        override val dataSourceName: String get() = "Time"
        override fun getType(): String = "float"

        override fun create(showResources: ShowResources): DataFeed = object : DataFeed {
            override fun set(uniform: Uniform) {
                val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f
                uniform.set(thisTime)
            }
        }
    }

    @Serializable
    data class UvCoordTexture(override val id: String) : DataSource {
        companion object : DataSourceBuilder<UvCoordTexture> {
            override val resourceName: String get() = "UvCoords"
            override fun suggestDataSources(inputPort: InputPort): List<UvCoordTexture> = emptyList()
            override fun build(inputPort: InputPort): UvCoordTexture = UvCoordTexture(inputPort.id)
        }

        override val dataSourceName: String get() = "U/V Coordinates Texture"
        override fun getType(): String = "sampler2D"

        override fun create(showResources: ShowResources): DataFeed =
            object : DataFeed, GlslRenderer.ArrangementListener {
                private val gl = showResources.glslContext
                private val uvCoordTextureUnit = gl.getTextureUnit(UvCoordTexture::class)
                private val uvCoordTexture = gl.check { createTexture() }

                override fun onArrangementChange(arrangement: GlslRenderer.Arrangement) {
                    if (arrangement.uvCoords.isEmpty()) return

                    val pixWidth = arrangement.pixWidth
                    val pixHeight = arrangement.pixHeight
                    val floatBuffer = FloatBuffer(arrangement.uvCoords)

                    with(uvCoordTextureUnit) {
                        bindTexture(uvCoordTexture)
                        configure(GL_NEAREST, GL_NEAREST)
                        uploadTexture(0, GL_R32F, pixWidth * 2, pixHeight, 0, GL_RED, GL_FLOAT, floatBuffer)
                    }
                }

                override fun set(uniform: Uniform) {
                    uniform.set(uvCoordTextureUnit)
                }

                override fun release() {
                    gl.check { deleteTexture(uvCoordTexture) }
                }
            }
    }

    @Serializable
    data class ScreenUvCoord(override val id: String) : DataSource {
        companion object : DataSourceBuilder<ScreenUvCoord> {
            override val resourceName: String get() = "U/V Coordinate"
            override fun suggestDataSources(inputPort: InputPort): List<ScreenUvCoord> = emptyList()
            override fun build(inputPort: InputPort): ScreenUvCoord = ScreenUvCoord(inputPort.id)
        }

        override val dataSourceName: String get() = "U/V Coordinate"
        override fun getType(): String = error("huh?")
        override fun isImplicit(): Boolean = true
        override fun getVarName(): String = "gl_FragCoord"

        override fun getRenderType(): String? = null

        override fun create(showResources: ShowResources): DataFeed {
            return object : DataFeed {
                override fun set(uniform: Uniform) {
                    // No-op.
                }
            }
        }
    }

    interface GadgetDataSource<T : baaahs.Gadget> : DataSource {
        fun createGadget(showResources: ShowResources): T

        fun set(gadget: T, uniform: Uniform)

        override fun create(showResources: ShowResources): DataFeed {
            val gadget = createGadget(showResources)
            showResources.createdGadget(id, gadget)
            return object : DataFeed {
                override fun set(uniform: Uniform) {
                    this@GadgetDataSource.set(gadget, uniform)
                }
            }
        }
    }

    @Serializable
    data class SliderDataSource(
        override val id: String,
        val title: String,
        val initialValue: Float,
        val minValue: Float,
        val maxValue: Float,
        val stepValue: Float
    ) : GadgetDataSource<Slider> {
        companion object : DataSourceBuilder<SliderDataSource> {
            override val resourceName: String get() = "Slider"
            override fun suggestDataSources(inputPort: InputPort): List<SliderDataSource> {
                return buildIf(inputPort.dataType == "float", inputPort)
            }

            override fun build(inputPort: InputPort): SliderDataSource {
                val config = inputPort.pluginConfig
                return SliderDataSource(
                    inputPort.id,
                    inputPort.title,
                    initialValue = config?.get("default")?.float ?: 1f,
                    minValue = config?.get("min")?.float ?: 0f,
                    maxValue = config?.get("max")?.float ?: 1f,
                    stepValue = config?.get("step")?.float ?: .01f
                )
            }
        }

        override val dataSourceName: String get() = resourceName
        override fun getType(): String = "float"
        override fun getRenderType(): String? = "Slider"

        override fun createGadget(showResources: ShowResources): Slider =
            Slider(title, initialValue, minValue, maxValue, stepValue)

        override fun set(gadget: Slider, uniform: Uniform) {
            uniform.set(gadget.value)
        }
    }

    @Serializable
    data class XyPad(
        override val id: String,
        val title: String,
        val varPrefix: String
    ) : DataSource {
        companion object : DataSourceBuilder<XyPad> {
            override val resourceName: String get() = "XyPad"
            override fun suggestDataSources(inputPort: InputPort): List<XyPad> {
                return buildIf(inputPort.dataType == "vec2", inputPort)
            }

            override fun build(inputPort: InputPort): XyPad =
                XyPad(inputPort.id, inputPort.title, inputPort.suggestVarName())
        }

        override val dataSourceName: String get() = "XY Pad"
        override fun getType(): String = "vec2"

        override fun create(showResources: ShowResources): DataFeed {
            return object : DataFeed {
                val xControl = showResources.useGadget<Slider>("${varPrefix}_x")
                val yControl = showResources.useGadget<Slider>("${varPrefix}_y")

                override fun set(uniform: Uniform) {
                    uniform.set(xControl.value, yControl.value)
                }
            }
        }
    }

    @Serializable
    data class ColorPickerProvider(
        override val id: String,
        val title: String,
        val initialValue: Color
    ) : GadgetDataSource<ColorPicker> {
        companion object : DataSourceBuilder<ColorPickerProvider> {
            override val resourceName: String get() = "ColorPicker"

            override fun suggestDataSources(inputPort: InputPort): List<ColorPickerProvider> {
                return buildIf(inputPort.dataType == "vec4", inputPort)
            }

            override fun build(inputPort: InputPort): ColorPickerProvider {
                val default = inputPort.pluginConfig?.get("default")?.primitive?.contentOrNull

                return ColorPickerProvider(
                    inputPort.id,
                    inputPort.title,
                    initialValue = default?.let { Color.from(it) } ?: Color.WHITE
                )
            }
        }

        override val dataSourceName: String get() = "Color Picker"
        override fun getType(): String = "vec4"
        override fun getRenderType(): String? = "ColorPicker"

        override fun createGadget(showResources: ShowResources): ColorPicker = ColorPicker(title, initialValue)

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
        override val id: String,
        val title: String,
        val options: List<String>,
        val initialSelectionIndex: Int
    ) : GadgetDataSource<RadioButtonStrip> {
        companion object : DataSourceBuilder<RadioButtonStripProvider> {
            override val resourceName: String get() = "Radio Button Strip"

            override fun suggestDataSources(inputPort: InputPort): List<RadioButtonStripProvider> {
                return buildIf(inputPort.dataType == "int", inputPort)
            }

            override fun build(inputPort: InputPort): RadioButtonStripProvider {
                val config = inputPort.pluginConfig

                val initialSelectionIndex = config?.getPrimitive("default")?.int ?: 0

                val options = config
                    ?.getArrayOrNull("options")
                    ?.map { it.primitive.content }
                    ?: error("no options given")

                return RadioButtonStripProvider(
                    inputPort.id,
                    inputPort.title,
                    options,
                    initialSelectionIndex
                )
            }
        }

        override val dataSourceName: String get() = resourceName
        override fun getType(): String = "int"

        override fun createGadget(showResources: ShowResources): RadioButtonStrip {
            return RadioButtonStrip(title, options, initialSelectionIndex)
        }

        override fun set(gadget: RadioButtonStrip, uniform: Uniform) {
            TODO("not implemented")
        }
    }

    @Serializable
    data class Scenes(
        override val id: String,
        val title: String = "Scenes"
    ) : GadgetDataSource<RadioButtonStrip> {
        companion object : DataSourceBuilder<Scenes> {
            override val resourceName: String get() = "SceneList"
            override fun suggestDataSources(inputPort: InputPort): List<Scenes> = emptyList()
            override fun build(inputPort: InputPort): Scenes = Scenes(inputPort.id)
        }

        override val dataSourceName: String get() = "Scene List"
        override fun isImplicit(): Boolean = true
        override fun getType(): String = error("huh?")
        override fun getRenderType(): String? = "SceneList"

        override fun createGadget(showResources: ShowResources): RadioButtonStrip {
            // TODO: this should be a custom gadget for scenes
            return RadioButtonStrip("Scenes", listOf("Scene 1", "Scene 2"), 0)
        }

        override fun set(gadget: RadioButtonStrip, uniform: Uniform) { /* No-pp.*/
        }
    }

    @Serializable
    data class Patches(
        override val id: String,
        val title: String = "Patches"
    ) : GadgetDataSource<RadioButtonStrip> {
        companion object : DataSourceBuilder<Patches> {
            override val resourceName: String get() = "PatchList"
            override fun suggestDataSources(inputPort: InputPort): List<Patches> = emptyList()
            override fun build(inputPort: InputPort): Patches = Patches(inputPort.id)

        }

        override val dataSourceName: String get() = "Patch List"
        override fun isImplicit(): Boolean = true
        override fun getType(): String = error("huh?")
        override fun getRenderType(): String? = "PatchList"

        override fun createGadget(showResources: ShowResources): RadioButtonStrip {
            // TODO: this should be a custom gadget for patches
            return RadioButtonStrip(title, listOf("Patch 1", "Patch 2"), 0)
        }

        override fun set(gadget: RadioButtonStrip, uniform: Uniform) { /* No-pp.*/
        }
    }

    companion object {
        val id = "baaahs.Core"

        val supportedContentTypes = mapOf(
            ContentType.UvCoordinateTexture to UvCoordTexture,
            ContentType.UvCoordinate to ScreenUvCoord,
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

        private fun <T : DataSource> DataSourceBuilder<T>.buildIf(matches: Boolean, inputPort: InputPort): List<T> {
            return if (matches) {
                listOf(build(inputPort))
            } else {
                emptyList()
            }
        }
    }
}