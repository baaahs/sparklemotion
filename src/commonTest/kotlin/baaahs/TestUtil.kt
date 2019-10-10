package baaahs

import baaahs.geom.Vector3F
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

class FakeClock(private var now: Time = 0.0) : Clock {
    override fun now(): Time = now
}

class TestModelSurface(override val name: String, override val expectedPixelCount: Int? = 1) : Model.Surface {
    override val description = name

    override fun allVertices(): Collection<Vector3F> {
        TODO("allVertices not implemented")
    }

    override val faces: List<Model.Face> = emptyList()
    override val lines: List<Model.Line> = emptyList()
}