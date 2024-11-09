package baaahs.control

import baaahs.describe
import baaahs.gadgets.Slider
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec

object SliderControlSpec : DescribeSpec({
    describe<SliderControl> {
        val slider by value { Slider("Brightness", 1f) }
        val feedId by value { "brightnezz" }
        val sliderControl by value { SliderControl("Brightness", controlledFeedId = feedId) }

//        it("allocates a Float channel") {
//            val channel = sliderControl.allocateChannel("brightness")
//            expect(channel.id).shouldBe("brightness")
//            expect(channel.initialValue).shouldBe(1f)
//            expect(channel.serializer).toBe(Float.serializer())
//        }
    }
})