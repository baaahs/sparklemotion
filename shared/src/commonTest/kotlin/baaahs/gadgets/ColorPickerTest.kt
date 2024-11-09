package baaahs.gadgets

import baaahs.Color
import baaahs.serializationRoundTrip
import io.kotest.matchers.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ColorPickerTest {
    @Test
    fun testColorPickerGadgetSerialization_stateShouldNotBeSent() {
        val slider = ColorPicker("name", Color(123456))
        slider.color = Color(654321)

        val otherColorPicker = serializationRoundTrip(ColorPicker.serializer(), slider)
        otherColorPicker.title.shouldBe("name")
        otherColorPicker.color.shouldBe(Color(123456))

        otherColorPicker.state.putAll(slider.state)
        otherColorPicker.color.shouldBe(Color(654321))
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
