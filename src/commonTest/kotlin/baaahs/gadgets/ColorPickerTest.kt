package baaahs.gadgets

import baaahs.Color
import baaahs.serializationRoundTrip
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.expect

class ColorPickerTest {
    @Test
    fun testColorPickerGadgetSerialization_stateShouldNotBeSent() {
        val slider = ColorPicker("name", Color(123456))
        slider.color = Color(654321)

        val otherColorPicker = serializationRoundTrip(ColorPicker.serializer(), slider)
        expect("name") { otherColorPicker.title }
        expect(Color(123456)) { otherColorPicker.color }

        otherColorPicker.state.putAll(slider.state)
        expect(Color(654321)) { otherColorPicker.color }
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
