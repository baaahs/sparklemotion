package baaahs

import baaahs.controller.ControllerId
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
import baaahs.gl.render.ComponentRenderEngine
import baaahs.gl.render.RenderTarget
import baaahs.gl.testToolchain
import baaahs.model.FakeModelEntity
import baaahs.model.Model
import baaahs.model.ModelData
import baaahs.model.SurfaceDataForTest
import baaahs.scene.ControllerConfig
import baaahs.scene.OpenControllerConfig
import baaahs.scene.OpenScene
import baaahs.scene.Scene
import baaahs.show.Shader
import baaahs.show.Stream
import baaahs.show.live.LinkedPatch
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import baaahs.util.Clock
import baaahs.util.asInstant
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals

@Suppress("UNCHECKED_CAST")
fun <T> nuffin(): T = null as T

fun MutableList<String>.assertEmpty() {
    this.shouldBeEmpty()
    this.clear()
}

fun MutableList<String>.assertContents(s: String) {
    this.shouldContainExactly(s)
    this.clear()
}

var json = Json { serializersModule = Gadget.serialModule }

fun <T> serializationRoundTrip(serializer: KSerializer<T>, obj: T): T {
    val jsonString = json.encodeToString(serializer, obj)
    return json.decodeFromString(serializer, jsonString)
}

fun <T : Any?> toBeSpecified(): T = error("override me!")

class FakeClock(
    var time: Instant = Instant.fromEpochMilliseconds(0),
    private val tz: TimeZone = TimeZone.of("America/New_York")
) : Clock {
    constructor(epochSeconds: Double) : this(epochSeconds.asInstant())

    override fun now(): Instant = time
    override fun tz(): TimeZone = tz

    companion object {
        fun now(): Instant = FakeClock().now()
    }
}

fun Instant.Companion.fromEpochSeconds(epochSeconds: Double) =
    fromEpochSeconds(
        epochSeconds.toLong(),
        (epochSeconds % 1.0 * 1_000_000_000).toInt()
    )

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

val TestModelSurfaceData = testModelSurfaceData("Panel")
val TestSceneData = Scene(
    ModelData("Test Model", listOf("panel1")),
    mapOf("panel1" to TestModelSurfaceData),
    emptyMap()
)
val TestModel = TestSceneData.open().model

fun modelForTest(entities: List<Model.Entity>) = Model("Test Model", entities)
fun modelForTest(vararg entities: Model.Entity) = Model("Test Model", entities.toList())
fun Model.openSceneForModel(controllerConfigs: Map<ControllerId, ControllerConfig>) =
    OpenScene(
        this,
        controllerConfigs.entries.associate { (id, config) ->
            val fixtureMappings = config.fixtures.map { it.open(this) }
            id to OpenControllerConfig(id, config, fixtureMappings)
        }
    )

class TestRenderContext(
    vararg val modelEntities: Model.Entity = arrayOf(FakeModelEntity("device1"))
) {
    val model = fakeModel(modelEntities.toList())
    val fixtureType = modelEntities.map { it.fixtureType }.distinct().only("fixture type")
    val gl = FakeGlContext()
    val renderEngine = ComponentRenderEngine(gl, fixtureType, minTextureWidth = 1)
    val showPlayer = FakeShowPlayer()
    val renderTargets = mutableListOf<RenderTarget>()

    fun createProgram(shaderSrc: String, incomingLinks: Map<String, ProgramNode>): GlslProgram {
        val openShader = testToolchain.openShader(Shader("Title", shaderSrc))
        val linkedPatch = LinkedPatch(openShader, incomingLinks, Stream.Main, 0f)
        val linkedProgram = ProgramLinker(linkedPatch).buildLinkedProgram()
        return renderEngine.compile(linkedProgram) { id, feed -> feed.open(showPlayer, id) }.bind()
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

    override fun allOff(): Unit = TODO("not implemented")
}

object ImmediateDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) = block.run()
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
