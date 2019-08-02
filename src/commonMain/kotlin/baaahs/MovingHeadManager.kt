package baaahs

import kotlin.js.JsName

class MovingHeadManager(val pubSub: PubSub.Server, movingHeads: List<MovingHead>) {
    private val movingHeadsChannel = pubSub.publish(Topics.movingHeads, movingHeads) { }
    private val defaultPosition = MovingHead.MovingHeadPosition(127, 127)
    private val currentPositions = mutableMapOf<MovingHead, MovingHead.MovingHeadPosition>()
    private val listeners = mutableMapOf<MovingHead, (MovingHead.MovingHeadPosition) -> Unit>()

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
