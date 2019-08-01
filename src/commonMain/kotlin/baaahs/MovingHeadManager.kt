package baaahs

import kotlin.js.JsName

class MovingHeadDisplay(val pubSub: PubSub.Client) {
    @JsName("subscribe")
    fun subscribe(name: String, callback: (MovingHead.MovingHeadPosition) -> Unit) =
        pubSub.subscribe(PubSub.Topic("movingHead/$name", MovingHead.MovingHeadPosition.serializer()), callback)
}
