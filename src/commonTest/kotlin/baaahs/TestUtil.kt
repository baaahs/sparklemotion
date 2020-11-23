package baaahs

import baaahs.fixtures.*
import baaahs.geom.Vector3F
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.ProgramLinker
import baaahs.gl.render.ModelRenderEngine
import baaahs.gl.render.RenderTarget
import baaahs.gl.testPlugins
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.show.live.LiveShaderInstance
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import baaahs.util.Clock
import baaahs.util.Time
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.logic._logicAppend
import ch.tutteli.atrium.logic.notToBe
import ch.tutteli.atrium.logic.toBe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.spekframework.spek2.dsl.GroupBody
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.meta.*
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.test.assertEquals

@Suppress("UNCHECKED_CAST")
fun <T> nuffin(): T = null as T

fun MutableList<String>.assertEmpty() {
    expect(this).isEmpty()
    this.clear()
}

fun MutableList<String>.assertContents(s: String) {
    expect(this).containsExactly(s)
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

class TestRenderContext(
    vararg val modelEntities: Model.Entity = arrayOf(FakeModelEntity("device1"))
) {
    val model = fakeModel(modelEntities.toList())
    val deviceType = modelEntities.map { it.deviceType }.distinct().only("device type")
    val gl = FakeGlContext()
    val renderEngine = ModelRenderEngine(gl, model, deviceType, minTextureWidth = 1)
    val glslAnalyzer = GlslAnalyzer(testPlugins())
    val showPlayer = FakeShowPlayer()
    val renderTargets = mutableListOf<RenderTarget>()

    fun createProgram(shaderSrc: String, incomingLinks: Map<String, LiveShaderInstance.DataSourceLink>): GlslProgram {
        val openShader = glslAnalyzer.openShader(shaderSrc)
        val liveShaderInstance = LiveShaderInstance(openShader, incomingLinks, null, 0f)
        val linkedPatch = ProgramLinker(liveShaderInstance).buildLinkedPatch()
        return renderEngine.compile(linkedPatch) { id, dataSource -> dataSource.createFeed(showPlayer, id) }
    }

    fun addFixtures() {
        renderTargets.addAll(
            modelEntities.map { entity ->
                renderEngine.addFixture(Fixture(entity, 1, emptyList(), deviceType, transport = NullTransport))
            }
        )
    }

    fun applyProgram(program: GlslProgram) {
        renderEngine.setRenderPlan(
            DeviceTypeRenderPlan(
                listOf(ProgramRenderPlan(program, renderTargets))
            )
        )
    }
}

expect fun assumeTrue(boolean: Boolean)

@Synonym(SynonymType.GROUP)
@Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
inline fun <reified T> GroupBody.describe(skip: Skip = Skip.No, noinline body: Suite.() -> Unit) {
    describe(T::class.toString(), skip, body)
}

@Synonym(SynonymType.GROUP)
@Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
fun GroupBody.describe(fn: KFunction<*>, skip: Skip = Skip.No, body: Suite.() -> Unit) {
    describe(fn.toString(), skip, body)
}

@Synonym(SynonymType.GROUP)
@Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
fun GroupBody.describe(prop: KProperty<*>, skip: Skip = Skip.No, body: Suite.() -> Unit) {
    describe(prop.toString(), skip, body)
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

fun <T> Expect<T>.toEqual(expected: T): Expect<T> = _logicAppend { toBe(expected) }
fun <T> Expect<T>.toNotEqual(expected: T): Expect<T> = _logicAppend { notToBe(expected) }
