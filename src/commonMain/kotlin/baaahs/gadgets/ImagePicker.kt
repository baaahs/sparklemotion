package baaahs.gadgets

import baaahs.Gadget
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlin.js.JsName

/** A gadget for picking an image. */
@Serializable
@SerialName("baaahs.Core:ImagePicker")
data class ImagePicker(
    /** The name for this color picker. */
    override val title: String,

    /** The initial value for this image picker. */
    val initialValue: ImageRef? = null
) : Gadget() {

    /** The selected color. */
    @JsName("image")
    var imageRef: ImageRef? by updatable("color", initialValue, ImageRef.serializer().nullable)

    override fun adjustALittleBit() {
//        fun randomAmount() = Random.nextFloat() * adjustmentFactor - .05f
//        imageRef = Color(
//            color.redF + randomAmount(),
//            color.greenF + randomAmount(),
//            color.blueF + randomAmount()
//        )
    }

    override fun adjustInRange(value: Float) {
//        val v = (value * 2 * PI).toFloat()
//        imageRef = Color(
//            sin(v) * .5f + .5f,
//            sin(v + PI / 2).toFloat() * .5f + .5f,
//            sin(v + PI).toFloat() * .5f + .5f
//        )
    }
}