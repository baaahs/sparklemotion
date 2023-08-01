package baaahs

import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureTypeRenderPlan
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
import baaahs.model.FakeModelEntity
import baaahs.model.Model
import baaahs.model.ModelData
import baaahs.model.SurfaceDataForTest
import baaahs.scene.OpenScene
import baaahs.scene.Scene
import baaahs.show.Shader
import baaahs.show.Stream
import baaahs.show.live.LinkedPatch
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
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

fun testModelSurface(
    name: String,
    expectedPixelCount: Int? = 1,
    vertices: List<Vector3F> = emptyList(),
    faces: List<Model.Face> = emptyList()
) = Model.Surface(name, name, expectedPixelCount, faces, emptyList(), Model.Geometry(vertices))

fun testModelSurfaceData(
    name: String,
    expectedPixelCount: Int? = 1,
    vertices: List<Vector3F> = emptyList()
) = SurfaceDataForTest(name, name, expectedPixelCount = expectedPixelCount, vertices = vertices)

fun fakeModel(vararg entities: Model.Entity) = modelForTest(entities.toList())
fun fakeModel(entities: List<Model.Entity>) = modelForTest(entities)

val TestModel = modelForTest(listOf(testModelSurface("Panel")))
val TestModelData = ModelData("Test Model", listOf(testModelSurfaceData("Panel")))
fun testScene(model: Model = TestModel) = OpenScene(model)
fun testSceneData(model: ModelData = TestModelData) = Scene(model)

fun modelForTest(entities: List<Model.Entity>) = Model("Test Model", entities)
fun modelForTest(vararg entities: Model.Entity) = Model("Test Model", entities.toList())

class TestRenderContext(
    vararg val modelEntities: Model.Entity = arrayOf(FakeModelEntity("device1"))
) {
    val model = fakeModel(modelEntities.toList())
    val fixtureType = modelEntities.map { it.fixtureType }.distinct().only("fixture type")
    val gl = FakeGlContext()
    val renderEngine = ModelRenderEngine(gl, fixtureType, minTextureWidth = 1,)
    val showPlayer = FakeShowPlayer()
    val renderTargets = mutableListOf<RenderTarget>()

    fun createProgram(shaderSrc: String, incomingLinks: Map<String, ProgramNode>): GlslProgram {
        val openShader = testToolchain.openShader(Shader("Title", shaderSrc))
        val linkedPatch = LinkedPatch(openShader, incomingLinks, Stream.Main, 0f)
        val linkedProgram = ProgramLinker(linkedPatch).buildLinkedProgram()
        return renderEngine.compile(linkedProgram) { id, feed -> feed.open(showPlayer, id) }
    }

    fun addFixtures() {
        renderTargets.addAll(
            modelEntities.map { entity ->
                val defaultConfig = fixtureType.defaultOptions.toConfig(entity, model, 1)
                renderEngine.addFixture(
                    Fixture(
                        entity, defaultConfig.componentCount, entity.name,
                        NullTransport, defaultConfig.fixtureType, defaultConfig
                    )
                )
            }
        )
    }

    fun applyProgram(program: GlslProgram) {
        renderEngine.setRenderPlan(
            FixtureTypeRenderPlan(
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
