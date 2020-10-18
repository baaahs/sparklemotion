package baaahs

import baaahs.geom.Vector3F
import baaahs.model.Model
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.spekframework.spek2.dsl.GroupBody
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.meta.*
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.expect

@Suppress("UNCHECKED_CAST")
fun <T> nuffin(): T = null as T

fun MutableList<String>.assertEmpty() {
    expect(emptyList<String>()) { this }
    this.clear()
}

fun MutableList<String>.assertContents(vararg s: String) {
    expect(s.toList()) { this }
    this.clear()
}

var json = Json { serializersModule = Gadget.serialModule }

fun <T> serializationRoundTrip(serializer: KSerializer<T>, obj: T): T {
    val jsonString = json.encodeToString(serializer, obj)
    return json.decodeFromString(serializer, jsonString)
}

fun <T: Any?> toBeSpecified(): T = error("override me!")

class FakeClock(var time: Time = 0.0) : Clock {
    override fun now(): Time = time
}

class TestModelSurface(
    override val name: String,
    override val expectedPixelCount: Int? = 1,
    val vertices: Collection<Vector3F> = emptyList()
) : Model.Surface {
    override val description = name

    override fun allVertices(): Collection<Vector3F> = vertices

    override val faces: List<Model.Face> = emptyList()
    override val lines: List<Model.Line> = emptyList()
}

expect fun assumeTrue(boolean: Boolean)

@Synonym(SynonymType.GROUP)
@Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
inline fun <reified T> GroupBody.describe(skip: Skip = Skip.No, noinline body: Suite.() -> Unit) {
    describe(T::class.toString(), skip, body)
}

fun expectEmptySet(block: () -> Set<*>) {
    val collection = block()
    assertEquals(0, collection.size, "Expected 0 items but have: $collection")
}

fun expectEmptyList(block: () -> List<*>) {
    val collection = block()
    assertEquals(0, collection.size, "Expected 0 items but have: $collection")
}

fun expectEmptyMap(block: () -> Map<*, *>) {
    val collection = block()
    assertEquals(0, collection.size, "Expected 0 items but have: ${collection.keys}")
}
