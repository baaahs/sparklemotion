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
import baaahs.ports.InputPortRef
import baaahs.ports.inputPortRef
import baaahs.show.DataSource
import com.danielgergely.kgl.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class CorePlugin : Plugin {
    override val packageName: String = "baaahs.Core"
    override val title: String = "SparkleMotion Core"

    override fun findDataSource(
        resourceName: String,
        inputPortRef: InputPortRef
    ): DataSource? {
        return when (resourceName) {
            "resolution" -> Resolution(inputPortRef.id)
            "time" -> Time(inputPortRef.id)
            "uvCoords" -> UvCoord(inputPortRef.id)

            "xyCoord" -> XyPad(inputPortRef)

            "Slider" -> SliderProvider(inputPortRef)
            "ColorPicker" -> ColorPickerProvider(inputPortRef)
            "RadioButtonStrip" -> RadioButtonStripProvider(inputPortRef)

            "Scenes" -> Scenes(inputPortRef)
            "Patches" -> Patches(inputPortRef)

            "none" -> NoOp(inputPortRef)
            "invalid" -> error("no provider for $inputPortRef")

            else -> throw IllegalArgumentException("unknown type $resourceName")
        }
    }

    @Serializable
    class NoOp : DataSource, DataFeed {
        constructor(inputPortRef: InputPortRef) {
            this.id = inputPortRef.id
            this.supportedTypes = listOf(inputPortRef.type)
        }

        override val id: String
        override val supportedTypes: List<String>

        override fun create(showResources: ShowResources): DataFeed = this

        override fun set(uniform: Uniform) {
            // no-op
        }
    }

    @Serializable
    class Resolution : DataSource, DataFeed, GlslProgram.ResolutionListener {
        constructor(id: String) {
            this.id = id
            this.supportedTypes = listOf("vec2")
        }

        override val id: String
        override val supportedTypes: List<String>

        @Transient
        var x = 1f
        @Transient
        var y = 1f

        override fun onResolution(x: Float, y: Float) {
            this.x = x
            this.y = y
        }

        override fun create(showResources: ShowResources): DataFeed = this

        override fun set(uniform: Uniform) = uniform.set(x, y)
    }

    @Serializable
    class Time : DataSource, DataFeed {
        constructor(id: String) {
            this.id = id
            this.supportedTypes = listOf("float")
        }

        override val id: String
        override val supportedTypes: List<String>

        override fun create(showResources: ShowResources): DataFeed = this

        override fun set(uniform: Uniform) {
            val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f
            uniform.set(thisTime)
        }
    }

    @Serializable
    class UvCoord : DataSource {
        constructor(id: String) {
            this.id = id
            this.supportedTypes = listOf("sampler2D")
        }

        override val id: String
        override val supportedTypes: List<String>

        override fun create(showResources: ShowResources): DataFeed =
            object : DataFeed, GlslRenderer.ArrangementListener {
                private val gl = showResources.glslContext
                private val uvCoordTextureUnit = gl.getTextureUnit(UvCoord::class)
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
    abstract class Gadget<T : baaahs.Gadget> : DataSource {
        abstract val inputPortRef: InputPortRef
        abstract fun createGadget(title: String, config: Map<String, String>): T
        abstract fun set(gadget: T, uniform: Uniform)

        override fun create(showResources: ShowResources): DataFeed {
            val varName = inputPortRef.varName
            val displayName = inputPortRef.title
            val gadget = createGadget(showResources)
            showResources.createdGadget(id, gadget)
            return object : DataFeed {
                override fun set(uniform: Uniform) {
                    this@Gadget.set(gadget, uniform)
                }
            }
        }

        open fun createGadget(showResources: ShowResources) =
            createGadget(inputPortRef.title, inputPortRef.pluginConfig)
    }

    @Serializable
    class SliderProvider : Gadget<Slider> {
        constructor(inputPortRef: InputPortRef) {
            this.id = inputPortRef.id
            this.inputPortRef = inputPortRef
        }

        override val id: String
        override val inputPortRef: InputPortRef
        override val supportedTypes: List<String> = listOf("float")
        override fun getRenderType(): String? = "Slider"

        override fun createGadget(title: String, config: Map<String, String>): Slider =
            Slider(
                title,
                initialValue = config["default"]?.toFloat() ?: 1f,
                minValue = config["min"]?.toFloat() ?: 0f,
                maxValue = config["max"]?.toFloat() ?: 1f,
                stepValue = config["step"]?.toFloat() ?: .01f
                // ,scale = config["scale"] ?: "linear"
            )

        override fun set(gadget: Slider, uniform: Uniform) {
            uniform.set(gadget.value)
        }
    }

    @Serializable
    class XyPad : DataSource {
        override val id: String

        constructor(inputPortRef: InputPortRef) {
            this.id = inputPortRef.id
            this.varName = inputPortRef.varName
            this.gadgetIdPrefix = "glsl_$varName"
            this.displayName = inputPortRef.title.capitalize()
            this.supportedTypes = listOf("vec2")
        }

        private val varName: String
        private val gadgetIdPrefix: String
        private val displayName: String

        override val supportedTypes: List<String>

        override fun create(showResources: ShowResources): DataFeed {
            return object : DataFeed {
                val xControl = showResources.useGadget<Slider>("${gadgetIdPrefix}_x")
                val yControl = showResources.useGadget<Slider>("${gadgetIdPrefix}_y")

                override fun set(uniform: Uniform) {
                    uniform.set(xControl.value, yControl.value)
                }
            }
        }
    }

    @Serializable
    class ColorPickerProvider : Gadget<ColorPicker> {
        constructor(inputPortRef: InputPortRef) {
            this.id = inputPortRef.id
            this.inputPortRef = inputPortRef
            this.supportedTypes = listOf("vec3", "vec4")
        }

        override val id: String
        override val inputPortRef: InputPortRef
        override val supportedTypes: List<String>
        override fun getRenderType(): String? = "ColorPicker"

        override fun createGadget(title: String, config: Map<String, String>): ColorPicker =
            ColorPicker(
                title,
                initialValue = config["default"]?.let { Color.from(it) } ?: Color.WHITE
            )

        override fun set(gadget: ColorPicker, uniform: Uniform) {
            val color = gadget.color
            when (inputPortRef.type) {
                "vec3" -> uniform.set(color.redF, color.greenF, color.blueF)
                "vec4" -> uniform.set(color.redF, color.greenF, color.blueF, color.alphaF)
            }
        }
    }

    @Serializable
    class RadioButtonStripProvider : Gadget<RadioButtonStrip> {
        constructor(inputPortRef: InputPortRef) {
            this.id = inputPortRef.id
            this.inputPortRef = inputPortRef
        }

        override val id: String
        override val inputPortRef: InputPortRef
        override val supportedTypes: List<String> = listOf("int")

        override fun createGadget(title: String, config: Map<String, String>): RadioButtonStrip {
            return RadioButtonStrip(title, config["options"] as List<String>, 0)
        }

        override fun set(gadget: RadioButtonStrip, uniform: Uniform) {
            TODO("not implemented")
        }
    }

    @Serializable
    class Scenes : Gadget<RadioButtonStrip> {
        constructor(id: InputPortRef) {
            this.id = inputPortRef.id
        }

        override val id: String
        override val inputPortRef: InputPortRef =
            inputPortRef("currentScene", "int", "Scenes")
        override val supportedTypes: List<String> = listOf("int")
        override fun getRenderType(): String? = "SceneList"

        override fun createGadget(title: String, config: Map<String, String>): RadioButtonStrip {
            // TODO: this should be a custom gadget for scenes
            return RadioButtonStrip(title, listOf("Scene 1", "Scene 2"), 0)
        }

        override fun set(gadget: RadioButtonStrip, uniform: Uniform) {
            TODO("not implemented")
        }
    }

    @Serializable
    class Patches : Gadget<RadioButtonStrip> {
        constructor(inputPortRef: InputPortRef) {
            this.id = inputPortRef.id
            this.inputPortRef = inputPortRef
        }

        override val id: String
        override val inputPortRef: InputPortRef
        override val supportedTypes: List<String> = listOf("int")
        override fun getRenderType(): String? = "PatchList"

        override fun createGadget(title: String, config: Map<String, String>): RadioButtonStrip {
            // TODO: this should be a custom gadget for patches
            return RadioButtonStrip(title, listOf("Patch 1", "Patch 2"), 0)
        }

        override fun set(gadget: RadioButtonStrip, uniform: Uniform) {
            TODO("not implemented")
        }
    }
}