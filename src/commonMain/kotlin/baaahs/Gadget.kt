package baaahs

import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.js.JsName
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

/**
 * Base class for user input gadgets.
 *
 * Gadgets may be requested by a [Show]:
 *
 * ```kotlin
 * val sparklinessSlider = showRunner.getGadget(Slider("Sparkliness"))
 *
 * fun nextFrame() {
 *   println("Sparkliness is ${sparklinessSlider.value}.")
 * }
 * ```
 */
abstract class Gadget {
    private val listeners = mutableSetOf<GadgetListener>()

    @JsName("listen")
    fun listen(gadgetListener: GadgetListener) {
        if (!listeners.add(gadgetListener)) throw IllegalStateException("$gadgetListener already listening to $this")
    }

    @JsName("unlisten")
    fun unlisten(gadgetListener: GadgetListener) {
        if (!listeners.remove(gadgetListener)) throw IllegalStateException("$gadgetListener isn't listening to $this")
    }

    @JsName("withoutTriggering")
    fun changed() = listeners.forEach { it.onChanged(this) }

    fun withoutTriggering(gadgetListener: GadgetListener, fn: () -> Unit) {
        unlisten(gadgetListener)
        try {
            fn()
        } finally {
            listen(gadgetListener)
        }
    }

    abstract fun toJson(): JsonElement
    abstract fun setFromJson(jsonElement: JsonElement)

    protected fun <T> watchForChanges(initialValue: T) = GadgetValueObserver(initialValue) { changed() }
}

interface GadgetListener {
    @JsName("onChanged")
    fun onChanged(gadget: Gadget)
}

class GadgetValueObserver<T>(initialValue: T, val onChange: () -> Unit) : ObservableProperty<T>(initialValue) {
    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        if (newValue != oldValue) onChange()
    }
}

@Serializable()
class GadgetData(@Polymorphic val gadget: Gadget, val topicName: String)

class GadgetDisplay(pubSub: PubSub.Client, onUpdatedGadgets: (Array<GadgetData>) -> Unit) {
    val activeGadgets = mutableListOf<GadgetData>()
    val channels = hashMapOf<String, PubSub.Observer<String>>()
    val jsonParser = Json(JsonConfiguration.Stable)

    init {
        pubSub.subscribe(Topics.activeGadgets) { gadgetDatas ->
            activeGadgets.clear()
            channels.forEach { it.value.unsubscribe() }
            channels.clear()

            gadgetDatas.forEach { gadgetData ->
                val gadget = gadgetData.gadget
                val topicName = gadgetData.topicName

                val listener = object : GadgetListener {
                    override fun onChanged(gadget: Gadget) {
                        val observer = channels[topicName]
                        if (observer == null) {
                            println("Huh, no observer for $topicName; discarding update (know about ${channels.keys})")
                        } else {
                            observer.onChange(gadget.toJson().toString())
                        }
                    }
                }
                gadget.listen(listener)

                channels[topicName] =
                    pubSub.subscribe(PubSub.Topic(topicName, String.serializer())) { json ->
                        gadget.apply { withoutTriggering(listener) { setFromJson(jsonParser.parseJson(json)) } }
                    }

                activeGadgets.add(gadgetData)
            }

            onUpdatedGadgets(activeGadgets.toTypedArray())
        }
    }
}

val gadgetModule = SerializersModule {
    polymorphic(Gadget::class) {
        ColorPicker::class with ColorPicker.serializer()
        Slider::class with Slider.serializer()
    }
}

