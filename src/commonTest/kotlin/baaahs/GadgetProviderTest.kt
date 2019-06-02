package baaahs

import baaahs.gadgets.Slider
import baaahs.sim.FakeNetwork
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.expect

class GadgetProviderTest {
    @Test
    fun whenOneGadgetStateUpdateFails_setGadgetsState_shouldStillUpdateTheRest() {
        val gadgetProvider = GadgetProvider(PubSub.Server(FakeNetwork().link(), 1234))

        val first = gadgetProvider.getGadget("first", Slider("first"))
            .apply { value = .123f }
        val second = gadgetProvider.getGadget("second", Slider("second"))
            .apply { value = .234f }
        val third = gadgetProvider.getGadget("third", Slider("third"))
            .apply { value = .345f }

        expect(gadgetProvider.getGadgetsState().toString()) {
            "{first={\"value\":0.123}, second={\"value\":0.234}, third={\"value\":0.345}}"
        }

        gadgetProvider.setGadgetsState(
            mapOf(
                "first" to JsonObject(mapOf("value" to JsonPrimitive(0.321))),
                "second" to JsonObject(mapOf("valueXXX" to JsonPrimitive(0.432))),
                "third" to JsonObject(mapOf("value" to JsonPrimitive(0.543)))
            )
        )

        expect(first.value) { 0.321f }
        expect(second.value) { 0.234f }
        expect(third.value) { 0.543f }
    }
}