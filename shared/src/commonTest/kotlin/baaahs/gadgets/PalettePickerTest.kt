package baaahs.gadgets

import baaahs.Color
import baaahs.serializationRoundTrip
import io.kotest.matchers.*
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PalettePickerTest {
    @Test
    fun testPalettePickerGadgetSerialization_stateShouldNotBeSent() {
        val palettePicker = PalettePicker("name", listOf(Color(123456)))
        palettePicker.colors = listOf(Color(654321), Color(234156))

        val otherPalettePicker = serializationRoundTrip(PalettePicker.serializer(), palettePicker)
        otherPalettePicker.title.shouldBe("name")
        otherPalettePicker.colors.shouldContainExactly(Color(123456))

        otherPalettePicker.state.putAll(palettePicker.state)
        otherPalettePicker.colors.shouldContainExactly(Color(654321), Color(234156))
    }

    @Test
    fun testEquality() {
        val colorSetA = listOf(Color(123456), Color(654321))

        val exemplar = PalettePicker("A", colorSetA)
        assertEquals(exemplar, PalettePicker("A", colorSetA))
        assertNotEquals(exemplar, PalettePicker("B", colorSetA))
        assertNotEquals(exemplar, PalettePicker("A", listOf(Color(654321))))
        assertNotEquals(exemplar, PalettePicker("A", listOf(Color(654321), Color(123456))))

        // Gadgets' current values don't affect their equality.
        assertEquals(exemplar, PalettePicker("A", colorSetA).apply { colors = listOf(Color(654321)) })
    }
}
