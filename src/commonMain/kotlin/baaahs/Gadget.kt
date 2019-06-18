package baaahs

import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlin.js.JsName
import kotlin.properties.ReadWriteProperty
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
 *
 * Mutable values in a gadget should be declared like this:
 *
 * ```kotlin
 *     var value: Float by updatable("value", initialValue, Float.serializer())
 * ```
 *
 * Mutable values _should not_ be included in tests for equality.
 */
open class Gadget {
    @Transient
    private val listeners = mutableSetOf<GadgetListener>()

    @JsName("listen")
    fun listen(gadgetListener: GadgetListener) {
        if (!listeners.add(gadgetListener)) throw IllegalStateException("$gadgetListener already listening to $this")
    }

    @JsName("unlisten")
    fun unlisten(gadgetListener: GadgetListener) {
        if (!listeners.remove(gadgetListener)) throw IllegalStateException("$gadgetListener isn't listening to $this")
    }

    fun changed() = listeners.forEach { it.invoke(this) }

    fun withoutTriggering(gadgetListener: GadgetListener, fn: () -> Unit) {
        unlisten(gadgetListener)
        try {
            fn()
        } finally {
            listen(gadgetListener)
        }
    }

    protected fun <T> updatable(name: String, initialValue: T, serializer: KSerializer<T>) =
        GadgetValueObserver(name, initialValue, serializer, state) { changed() }

    val state: MutableMap<String, JsonElement> = hashMapOf()
}

typealias GadgetListener = (Gadget) -> Unit

class GadgetValueObserver<T>(
    val name: String,
    val initialValue: T,
    private val serializer: KSerializer<T>,
    val data: MutableMap<String, JsonElement>,
    val onChange: () -> Unit
) : ReadWriteProperty<Gadget, T> {
    override fun getValue(thisRef: Gadget, property: KProperty<*>): T {
        val value = data[name]
        return if (value == null) initialValue else {
            jsonParser.fromJson(serializer, value)
        }
    }

    override fun setValue(thisRef: Gadget, property: KProperty<*>, value: T) {
        if (getValue(thisRef, property) != value) {
            data[name] = jsonParser.toJson(serializer, value)
            onChange()
        }
    }
}

@Serializable()
class GadgetData(val name: String, @Polymorphic var gadget: Gadget, val topicName: String)

val GadgetDataSerializer = (String.serializer() to JsonElement.serializer()).map

class GadgetDisplay(pubSub: PubSub.Client, onUpdatedGadgets: (Array<GadgetData>) -> Unit) {
    val activeGadgets = mutableListOf<GadgetData>()
    val channels = hashMapOf<String, PubSub.Channel<Map<String, JsonElement>>>()

    init {
        pubSub.subscribe(Topics.activeGadgets) { gadgetDatas ->
            activeGadgets.clear()
            channels.forEach { it.value.unsubscribe() }
            channels.clear()

            gadgetDatas.forEach { gadgetData ->
                val gadget = gadgetData.gadget
                val topicName = gadgetData.topicName

                val listener: GadgetListener = {
                    val observer = channels[topicName]
                    if (observer == null) {
                        println("Huh, no observer for $topicName; discarding update (know about ${channels.keys})")
                    } else {
                        observer.onChange(it.state)
                    }
                }
                gadget.listen(listener)

                channels[topicName] =
                    pubSub.subscribe(PubSub.Topic(topicName, GadgetDataSerializer)) { json ->
                        gadget.apply {
                            withoutTriggering(listener) {
                                gadget.state.putAll(json)
                                gadget.changed()
                            }
                        }
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

private val jsonParser = Json(JsonConfiguration.Stable)
