package baaahs

import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.fixtures.DeviceTypeRenderPlan
import baaahs.fixtures.Fixture
import baaahs.fixtures.NullTransport
import baaahs.fixtures.ProgramRenderPlan
import baaahs.geom.Vector3F
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.openShader
import baaahs.gl.patch.ProgramLinker
import baaahs.gl.patch.ProgramNode
import baaahs.gl.render.ModelRenderEngine
import baaahs.gl.render.RenderTarget
import baaahs.gl.testToolchain
import baaahs.model.Model
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.live.LinkedShaderInstance
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import baaahs.sim.FixtureSimulation
import baaahs.sim.SimulationEnv
import baaahs.util.Clock
import baaahs.util.Time
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.verbs.expect
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.domain.builders.reporting.ReporterBuilder
import ch.tutteli.atrium.logic._logicAppend
import ch.tutteli.atrium.logic.notToBe
import ch.tutteli.atrium.logic.toBe
import ch.tutteli.atrium.reporting.Reporter
import ch.tutteli.atrium.reporting.ReporterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.spekframework.spek2.dsl.GroupBody
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.meta.*
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.coroutines.CoroutineContext
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
) : Model.Entity {
    override val bounds: Pair<Vector3F, Vector3F>
        get() = Vector3F.origin to Vector3F.origin

    override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation {
        TODO("not implemented")
    }
}

class TestModelSurface(
    name: String,
    expectedPixelCount: Int? = 1,
    private val vertices: List<Vector3F> = emptyList()
) : Model.Surface(name, name, expectedPixelCount, emptyList(), emptyList(), Model.Geometry(emptyList())) {
    override fun allVertices(): Collection<Vector3F> = vertices
}

fun fakeModel(vararg entities: Model.Entity) = ModelForTest(entities.toList())
fun fakeModel(entities: List<Model.Entity>) = ModelForTest(entities)

object TestModel : ModelForTest(listOf(TestModelSurface("Panel")))

open class ModelForTest(private val entities: List<Entity>) : Model() {
    constructor(vararg entities: Entity) : this(entities.toList())

    override val name: String = "Test Model"
    override val allEntities: List<Entity> get() = entities
}

class TestRenderContext(
    vararg val modelEntities: Model.Entity = arrayOf(FakeModelEntity("device1"))
) {
    val model = fakeModel(modelEntities.toList())
    val deviceType = modelEntities.map { it.deviceType }.distinct().only("device type")
    val gl = FakeGlContext()
    val renderEngine = ModelRenderEngine(gl, model, deviceType, minTextureWidth = 1,)
    val showPlayer = FakeShowPlayer()
    val renderTargets = mutableListOf<RenderTarget>()

    fun createProgram(shaderSrc: String, incomingLinks: Map<String, ProgramNode>): GlslProgram {
        val openShader = testToolchain.openShader(Shader("Title", shaderSrc))
        val liveShaderInstance = LinkedShaderInstance(openShader, incomingLinks, ShaderChannel.Main, 0f)
        val linkedPatch = ProgramLinker(liveShaderInstance).buildLinkedPatch()
        return renderEngine.compile(linkedPatch) { id, dataSource -> dataSource.createFeed(showPlayer, id) }
    }

    fun addFixtures() {
        renderTargets.addAll(
            modelEntities.map { entity ->
                renderEngine.addFixture(Fixture(entity, 1, emptyList(), deviceType.defaultConfig, transport = NullTransport))
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

class FakeDmxManager(private val universe: Dmx.Universe) : DmxManager {
    override val dmxUniverse: Dmx.Universe get() = universe

    override fun allOff():Unit = TODO("not implemented")
}

object ImmediateDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) = block.run()
}


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

fun Expect<JsonObject>.toEqualJson(expected: String): Expect<JsonObject> =
    _logicAppend { toBe(Json {}.parseToJsonElement(expected) as JsonObject) }

fun useBetterSpekReporter() = BetterSpekReporterFactory()
class BetterSpekReporterFactory : ReporterFactory {
    init {
        // we specify the factory here because we only need to specify it once and
        // we do not want to specify it if it is not used. The verbs have to be loaded on their first usage
        // and thus this is a good place.
        ReporterFactory.specifyFactoryIfNotYetSet(ID)
    }

    override val id: String = ID

    override fun create(): Reporter {
        return ReporterBuilder.create()
            .withoutTranslationsUseDefaultLocale()
            .withDetailedObjectFormatter()
            .withDefaultAssertionFormatterController()
            .withDefaultAssertionFormatterFacade()
            .withTextNextLineAssertionPairFormatter()
            .withTextCapabilities()
            .withNoOpAtriumErrorAdjuster()
            .withOnlyFailureReporter()
            .build()
    }

    companion object {
        const val ID: String = "baaahs.default"
    }
}
