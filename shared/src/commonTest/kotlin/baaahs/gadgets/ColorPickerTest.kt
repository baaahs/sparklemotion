package baaahs.gadgets

import baaahs.Color
import baaahs.serializationRoundTrip
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ColorPickerTest {
    @Test
    fun testColorPickerGadgetSerialization_stateShouldNotBeSent() {
        val slider = ColorPicker("name", Color(123456))
        slider.color = Color(654321)

        val otherColorPicker = serializationRoundTrip(ColorPicker.serializer(), slider)
        expect(otherColorPicker.title).toBe("name")
        expect(otherColorPicker.color).toBe(Color(123456))

        otherColorPicker.state.putAll(slider.state)
        expect(otherColorPicker.color).toBe(Color(654321))
    }

    @Test
    fun testEquality() {
        val exemplar = ColorPicker("A", Color(123456))
        assertEquals(exemplar, ColorPicker("A", Color(123456)))
        assertNotEquals(exemplar, ColorPicker("B", Color(123456)))
        assertNotEquals(exemplar, ColorPicker("A", Color(654321)))

        // Gadgets' current values don't affect their equality.
        assertEquals(exemplar, ColorPicker("A", Color(123456)).apply { color = Color(654321) })
    }
}
