package baaahs

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.test.expect

fun MutableList<String>.assertEmpty() {
    expect(emptyList<String>()) { this }
    this.clear()
}

fun MutableList<String>.assertContents(vararg s: String) {
    expect(s.toList()) { this }
    this.clear()
}

var json = Json(JsonConfiguration.Stable, gadgetModule)

fun <T> serializationRoundTrip(serializer: KSerializer<T>, obj: T): T {
    val jsonString = json.stringify(serializer, obj)
    return json.parse(serializer, jsonString)
}

class FakeClock(var now: Time = 0.0) : Clock {
    override fun now(): Time = now
}   