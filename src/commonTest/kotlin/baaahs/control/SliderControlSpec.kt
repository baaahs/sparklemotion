package baaahs.control

import baaahs.describe
import baaahs.gadgets.Slider
import org.spekframework.spek2.Spek

object SliderControlSpec : Spek({
    describe<SliderControl> {
        val slider by value { Slider("Brightness", 1f) }
        val feedId by value { "brightnezz" }
        val sliderControl by value { SliderControl("Brightness", controlledFeedId = feedId) }

//        it("allocates a Float channel") {
//            val channel = sliderControl.allocateChannel("brightness")
//            expect(channel.id).toEqual("brightness")
//            expect(channel.initialValue).toEqual(1f)
//            expect(channel.serializer).toBe(Float.serializer())
//        }
    }
})