package baaahs

import baaahs.io.Fs
import baaahs.model.MovingHead
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.js.JsName

class MovingHeadManager(private val fs: Fs, private val pubSub: PubSub.Server, movingHeads: List<MovingHead>) {
    private val movingHeadsChannel = pubSub.publish(Topics.movingHeads, movingHeads) { }
    private val defaultPosition = MovingHead.MovingHeadPosition(127, 127)
    private val currentPositions = mutableMapOf<MovingHead, MovingHead.MovingHeadPosition>()
    private val listeners = mutableMapOf<MovingHead, (MovingHead.MovingHeadPosition) -> Unit>()

    private val movingHeadPresets = mutableMapOf<String, MovingHead.MovingHeadPosition>()
    private val json = Json(JsonConfiguration.Stable)

    private val presetsFileName = fs.resolve("presets/moving-head-positions.json")

    init {
        val presetsJson = fs.loadFile(presetsFileName)
        if (presetsJson != null) {
            val map = json.parse(Topics.movingHeadPresets.serializer, presetsJson)
            movingHeadPresets.putAll(map)
        }
    }

    private val movingHeadPresetsChannel =
        pubSub.publish(
            Topics.movingHeadPresets, mutableMapOf(
                "Disco Balls" to MovingHead.MovingHeadPosition(123, 200)
            )
        ) { map ->
            fs.saveFile(presetsFileName, json.stringify(Topics.movingHeadPresets.serializer, map), true)
            println("Saved $map to disk!")
        }

    init {
        movingHeads.map { movingHead ->
            val topic = PubSub.Topic("movingHead/${movingHead.name}", MovingHead.MovingHeadPosition.serializer())

            currentPositions[movingHead] = defaultPosition
            pubSub.publish(topic, defaultPosition) { onUpdate ->
                currentPositions[movingHead] = onUpdate
                listeners[movingHead]?.invoke(onUpdate)
            }
        }
    }

    fun listen(movingHead: MovingHead, onUpdate: (MovingHead.MovingHeadPosition) -> Unit) {
        listeners[movingHead] = onUpdate
    }
}

class MovingHeadDisplay(val pubSub: PubSub.Client, onUpdatedMovingHeads: (Array<Wrapper>) -> Unit) {
    init {
        pubSub.subscribe(Topics.movingHeads) { movingHeads ->
            val wrappers = movingHeads.map { movingHead -> Wrapper(movingHead, pubSub) }
            onUpdatedMovingHeads(wrappers.toTypedArray())
        }
    }

    private val presets = mutableMapOf<String, MovingHead.MovingHeadPosition>()
    private val presetsListeners = mutableListOf<(String) -> Unit>()

    private val movingHeadPresetsChannel =
        pubSub.subscribe(Topics.movingHeadPresets) { map ->
            presets.clear()
            presets.putAll(map)
            notifyPresetsListeners()
        }

    private fun notifyPresetsListeners() {
        val json = Json.stringify(Topics.movingHeadPresets.serializer, presets)
        presetsListeners.forEach { it.invoke(json) }
    }

    @JsName("savePreset")
    fun savePreset(name: String, position: MovingHead.MovingHeadPosition) {
        presets[name] = position
        movingHeadPresetsChannel.onChange(presets)
        notifyPresetsListeners()
    }

    @JsName("addPresetsListener")
    fun addPresetsListener(callback: (String) -> Unit) {
        presetsListeners.add(callback)
    }

    @JsName("removePresetsListener")
    fun removePresetsListener(callback: (String) -> Unit) {
        presetsListeners.remove(callback)
    }

    class Wrapper(val movingHead: MovingHead, pubSub: PubSub.Client) {
        private val topic = PubSub.Topic("movingHead/${movingHead.name}", MovingHead.MovingHeadPosition.serializer())
        private val listeners = mutableListOf<(MovingHead.MovingHeadPosition) -> Unit>()
        private val channel: PubSub.Channel<MovingHead.MovingHeadPosition>? =
            pubSub.subscribe(topic) { onUpdate ->
                // TODO: the second time a moving head editor opens, this fires before channel has been set;
                // TODO: onUpdate should be deferred until after subscribe() exits.
                position = onUpdate
            }

        @JsName("name")
        val name: String
            get() = movingHead.name

        @JsName("position")
        var position: MovingHead.MovingHeadPosition? = null
            set(value) {
                field = value
                if (value != null) notifyListeners(value)
            }

        private fun notifyListeners(value: MovingHead.MovingHeadPosition) {
            // TODO: channel.onChange() is causing circular updates to PubSub server
            channel?.onChange(value)
            listeners.forEach { it.invoke(value) }
        }

        @JsName("addListener")
        fun addListener(callback: (MovingHead.MovingHeadPosition) -> Unit) {
            listeners.add(callback)
        }

        @JsName("removeListener")
        fun removeListener(callback: (MovingHead.MovingHeadPosition) -> Unit) {
            listeners.remove(callback)
        }
    }

}
