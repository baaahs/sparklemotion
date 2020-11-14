package baaahs

import baaahs.fixtures.DeviceType
import baaahs.fixtures.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.util.Clock
import baaahs.util.Time
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

class FakeModelEntity(
    override val name: String,
    override val deviceType: DeviceType = PixelArrayDevice,
    override val description: String = name
) : Model.Entity

class TestModelSurface(
    name: String,
    expectedPixelCount: Int? = 1,
    private val vertices: Collection<Vector3F> = emptyList()
) : Model.Surface(name, name, PixelArrayDevice, expectedPixelCount, emptyList(), emptyList()) {
    override fun allVertices(): Collection<Vector3F> = vertices
}

fun fakeModel(entities: List<Model.Entity>) = ModelForTest(entities)

object TestModel : ModelForTest(listOf(TestModelSurface("Panel")))

open class ModelForTest(private val entities: List<Entity>) : Model() {
    override val name: String = "Test Model"
    override val movingHeads: List<MovingHead> get() = entities.filterIsInstance<MovingHead>()
    override val allSurfaces: List<Surface> get() = entities.filterIsInstance<Surface>()
    override val allEntities: List<Entity> get() = entities
    override val geomVertices: List<Vector3F> = emptyList()

    override val center: Vector3F
        get() = Vector3F(.5f, .5f, .5f)
    override val extents: Vector3F
        get() = Vector3F(1f, 1f, 1f)
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
