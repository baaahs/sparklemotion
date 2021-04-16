package baaahs.gadgets

import baaahs.serializationRoundTrip
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SliderTest {
    @Test
    fun testSliderGadgetSerialization_stateShouldNotBeSent() {
        val slider = Slider("name", .25f)
        slider.position = .75f

        val otherSlider = serializationRoundTrip(Slider.serializer(), slider)
        expect(otherSlider.title).toBe("name")
        expect(otherSlider.position).toBe(.25f)

        otherSlider.state.putAll(slider.state)
        expect(otherSlider.position).toBe(.75f)
    }

    @Test
    fun testEquality() {
        val exemplar = Slider("A", .25f)
        assertEquals(exemplar, Slider("A", .25f))
        assertNotEquals(exemplar, Slider("B", .25f))
        assertNotEquals(exemplar, Slider("A", .75f))

        // Gadgets' current values don't affect their equality.
        assertEquals(exemplar, Slider("A", .25f).apply { position = .75f })
    }
}
