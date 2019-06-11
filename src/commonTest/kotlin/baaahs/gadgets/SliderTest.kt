package baaahs.gadgets

import baaahs.serializationRoundTrip
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.expect

class SliderTest {
    @Test
    fun testSliderGadgetSerialization_stateShouldNotBeSent() {
        val slider = Slider("name", .25f)
        slider.value = .75f

        val otherSlider = serializationRoundTrip(Slider.serializer(), slider)
        expect("name") { otherSlider.name }
        expect(.25f) { otherSlider.value }

        otherSlider.state.putAll(slider.state)
        expect(.75f) { otherSlider.value }
    }

    @Test
    fun testEquality() {
        val exemplar = Slider("A", .25f)
        assertEquals(exemplar, Slider("A", .25f))
        assertNotEquals(exemplar, Slider("B", .25f))
        assertNotEquals(exemplar, Slider("A", .75f))

        // Gadgets' current values don't affect their equality.
        assertEquals(exemplar, Slider("A", .25f).apply { value = .75f })
    }
}
