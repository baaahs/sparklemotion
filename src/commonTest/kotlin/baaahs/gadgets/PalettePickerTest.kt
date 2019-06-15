package baaahs.gadgets

import baaahs.Color
import baaahs.serializationRoundTrip
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.expect

class PalettePickerTest {
    @Test
    fun testPalettePickerGadgetSerialization_stateShouldNotBeSent() {
        val palettePicker = PalettePicker("name", arrayOf(Color(123456)))
        palettePicker.colors = arrayOf(Color(654321), Color(234156))

        val otherPalettePicker = serializationRoundTrip(PalettePicker.serializer(), palettePicker)
        expect("name") { otherPalettePicker.name }
        expect(arrayOf(Color(123456)).toList()) { otherPalettePicker.colors.toList() }

        otherPalettePicker.state.putAll(palettePicker.state)
        expect(arrayOf(Color(654321), Color(234156))) { otherPalettePicker.colors }
    }

    @Test
    fun testEquality() {
        val colorSetA = arrayOf(Color(123456), Color(654321))

        val exemplar = PalettePicker("A", colorSetA)
        assertEquals(exemplar, PalettePicker("A", colorSetA))
        assertNotEquals(exemplar, PalettePicker("B", colorSetA))
        assertNotEquals(exemplar, PalettePicker("A", arrayOf(Color(654321))))
        assertNotEquals(exemplar, PalettePicker("A", arrayOf(Color(654321), Color(123456))))

        // Gadgets' current values don't affect their equality.
        assertEquals(exemplar, PalettePicker("A", colorSetA).apply { colors = arrayOf(Color(654321)) })
    }
}
