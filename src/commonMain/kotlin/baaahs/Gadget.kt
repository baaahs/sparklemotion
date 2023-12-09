package baaahs

import baaahs.gadgets.*
import baaahs.ui.IObservable
import baaahs.ui.Observable
import baaahs.ui.Observer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.collections.set
import kotlin.js.JsName
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Base class for user input gadgets.
 *
 * Mutable values in a gadget should be declared like this:
 *
 * ```kotlin
 *     var value: Float by updatable("value", initialValue, Float.serializer())
 * ```
 *
 * Mutable values _should not_ be included in tests for equality.
 */
@Serializable
@Polymorphic
abstract class Gadget {
    abstract val title: String

    @Transient
    private val listeners = arrayListOf<Listener>()

    protected val adjustmentFactor get() = 1f / 64

    @JsName("listen")
    fun listen(gadgetListener: GadgetListener): GadgetListener {
        if (findListener(gadgetListener) != -1)
            throw IllegalStateException("$gadgetListener already listening to $this")
        listeners.add(Listener(gadgetListener))
        return gadgetListener
    }

    @JsName("unlisten")
    fun unlisten(gadgetListener: GadgetListener) {
        val i = findListener(gadgetListener)
        if (i == -1)
            throw IllegalStateException("$gadgetListener isn't listening to $this")
        listeners.removeAt(i)
    }

    private fun findListener(gadgetListener: GadgetListener): Int {
        return listeners.indexOfFirst { it.callback === gadgetListener }
    }

    fun changed() = listeners.forEach {
        if (it.enabled) it.callback.invoke(this)
    }

    fun withoutTriggering(gadgetListener: GadgetListener?, fn: () -> Unit) {
        if (gadgetListener == null) {
            fn()
        } else {
            val listener = listeners.find { it.callback == gadgetListener }
//                ?: throw IllegalStateException("$gadgetListener isn't listening to $this")

            if (listener == null) {
                fn()
            } else {
                val priorEnabled = listener.enabled
                listener.enabled = false
                try {
                    fn()
                } finally {
                    listener.enabled = priorEnabled
                }
            }
        }
    }

    val observable: IObservable
        get() = object : Observable() {
            private val gadgetListener: GadgetListener = { notifyChanged() }

            override fun addObserver(observer: Observer): Observer {
                if (!anyObservers()) listen(gadgetListener)
                return super.addObserver(observer)
            }

            override fun removeObserver(observer: Observer) {
                super.removeObserver(observer)
                if (!anyObservers()) unlisten(gadgetListener)
            }
        }

    protected fun <T> updatable(name: String, initialValue: T, serializer: KSerializer<T>): ReadWriteProperty<Gadget, T> =
        GadgetValueObserver(name, initialValue, serializer) { changed() }

    /**
     * Implementing child classes should change their state a little in some valid way, as if a user had done it.
     */
    open fun adjustALittleBit() {}

    /**
     * Implementing child classes should change their state scaled to reflect the given value in the range [0..1).
     */
    open fun adjustInRange(value: Float) {}

    @Transient
    val state: MutableMap<String, JsonElement> = hashMapOf()

    /**
     * Applies initial state.
     */
    fun applyState(newState: Map<String, JsonElement>) {
        val priorState = HashMap(state)
        state.putAll(newState)

        if (state != priorState)
            changed()
    }

    private class Listener(val callback: GadgetListener, var enabled: Boolean = true)

    companion object {
        val serialModule = SerializersModule {
            polymorphic(Gadget::class) {
                subclass(ColorPicker::class, ColorPicker.serializer())
                subclass(ImagePicker::class, ImagePicker.serializer())
                subclass(PalettePicker::class, PalettePicker.serializer())
                subclass(Slider::class, Slider.serializer())
                subclass(Switch::class, Switch.serializer())
                subclass(XyPad::class, XyPad.serializer())
            }
        }
    }
}

typealias GadgetListener = (Gadget) -> Unit

private class GadgetValueObserver<T>(
    val name: String,
    val initialValue: T,
    private val serializer: KSerializer<T>,
    val onChange: () -> Unit
) : ReadWriteProperty<Gadget, T> {
    override fun getValue(thisRef: Gadget, property: KProperty<*>): T {
        val value = thisRef.state[name]
        return if (value == null) initialValue else {
            jsonParser.decodeFromJsonElement(serializer, value)
        }
    }

    override fun setValue(thisRef: Gadget, property: KProperty<*>, value: T) {
        if (getValue(thisRef, property) != value) {
            thisRef.state[name] = jsonParser.encodeToJsonElement(serializer, value)
            onChange()
        }
    }
}

@Serializable
class SetGadgetStateCommand(val id: String, val state: Map<String, JsonElement>)

class ShowControlCommands(serialModule: SerializersModule) {
    val setGadgetStateCommand = PubSub.CommandPort(
        "pinky/showControl/setGadgetState", SetGadgetStateCommand.serializer(),
        Unit.serializer(), serialModule
    )
}

val GadgetDataSerializer = MapSerializer(String.serializer(), JsonElement.serializer())

private val jsonParser = Json
