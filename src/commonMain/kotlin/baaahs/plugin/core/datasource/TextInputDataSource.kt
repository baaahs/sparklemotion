package baaahs.plugin.core.datasource

import baaahs.ShowPlayer
import baaahs.control.MutableTextInputControl
import baaahs.gadgets.TextInput
import baaahs.gadgets.ImagePicker
import baaahs.gadgets.ImageRef
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.imaging.Image
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.mutable.MutableControl
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import baaahs.util.globalLaunch
import com.danielgergely.kgl.GL_LINEAR
import com.danielgergely.kgl.GL_RGBA
import com.danielgergely.kgl.GL_UNSIGNED_BYTE
import com.danielgergely.kgl.Texture
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("baaahs.Core:TextInput")
data class TextInputDataSource(override val title: String) : DataSource {
    override val pluginPackage: String get() = CorePlugin.id
    override fun getType(): GlslType = GlslType.Vec4
    override val contentType: ContentType get() = ContentType.Color

    fun createGadget(): TextInput =
        TextInput(title)

    override fun buildControl(): MutableControl =
        MutableTextInputControl(title, "", this)

    override fun appendDeclaration(buf: StringBuilder, id: String) {
        val textureUniformId = "ds_${getVarName(id)}_texture"
        /**language=glsl*/
        buf.append("uniform sampler2D $textureUniformId;\n")
    }

    override fun appendInvoke(buf: StringBuilder, varName: String, inputPort: InputPort) {
        val fn = inputPort.glslArgSite as GlslCode.GlslFunction

        val textureUniformId = "ds_${getVarName(varName)}_texture"
        val uvParamName = fn.params[0].name
        buf.append("texture($textureUniformId, vec2($uvParamName.x, 1. - $uvParamName.y))")
    }

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        val imagePicker = showPlayer.useGadget(this)
            ?: run {
                logger.debug { "No control gadget registered for datasource $id, creating one. This is probably busted." }
                createGadget()
            }

        return object : Feed, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                private val textureUnit = gl.getTextureUnit(id)
                private val texture = gl.check { createTexture() }

                override fun bind(glslProgram: GlslProgram): ProgramFeed =
                    ImageProgramFeed(glslProgram, getVarName(id), imagePicker, texture, textureUnit)
            }
        }
    }

    class ImageProgramFeed(
        glslProgram: GlslProgram,
        varName: String,
        private val textInput: TextInput,
        private val texture: Texture,
        private val textureUnit: GlContext.TextureUnit
    ) : ProgramFeed {
        private val textureId = "ds_${varName}_texture"
        private val textureUniform = glslProgram.getUniform(textureId)
        override val isValid: Boolean
            get() = textureUniform != null

        // private var imageRef: ImageRef? = null
        private var image: Image? = null
        private var textToDisplay: String = ""
        

        // UPDATE THIS
        override fun setOnProgram() {
            println("set on program")
            println("textInput.value: ${textInput.value}")
            if (textInput.value.length > 0 && textInput.value != textToDisplay) {

              
              // image.drawText(textInput.value)
                // imageRef = imagePicker.imageRef
                // val imageRef = imageRef
                // globalLaunch {
                //     image = when (imageRef) {
                //         is ImageRef.Local -> Image.fromDataUrl(imageRef.dataUrl)
                //         null -> null
                //         else -> error("Unsupported nmageRef type $imageRef")
                //     }
                // }
            }
            val image = image
            if (image?.hasChanged() == true) {
                val bitmap = image.toBitmap()
                with(textureUnit) {
                    bindTexture(texture)
                    configure(GL_LINEAR, GL_LINEAR)

                    bitmap.withGlBuffer { buf ->
                        uploadTexture(
                            0, GL_RGBA, image.width, image.height, 0,
                            GL_RGBA, GL_UNSIGNED_BYTE, buf
                        )
                    }
                }
                textureUniform?.set(textureUnit)
            }
        }
    }

    companion object : DataSourceBuilder<TextInputDataSource> {
        override val title: String get() = "TextInput"
        override val description: String get() = "A user-provided string."
        override val resourceName: String get() = "TextInput"
        override val contentType: ContentType get() = ContentType.Color
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.dataTypeIs(GlslType.Sampler2D) // TODO: Should be vec4/3 instead?
        override fun build(inputPort: InputPort): TextInputDataSource =
            TextInputDataSource("${inputPort.title} TextInput")

        override fun funDef(varName: String): String =
            "vec4 $varName(vec2 uv);"

        private val logger = Logger<TextInputDataSource>()
    }
}
