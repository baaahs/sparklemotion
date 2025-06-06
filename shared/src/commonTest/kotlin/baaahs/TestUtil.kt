package baaahs

import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureTypeRenderPlan
import baaahs.fixtures.NullTransport
import baaahs.fixtures.ProgramRenderPlan
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.openShader
import baaahs.gl.patch.ProgramLinker
import baaahs.gl.patch.ProgramNode
import baaahs.gl.render.ComponentRenderEngine
import baaahs.gl.render.RenderTarget
import baaahs.gl.testPlugins
import baaahs.gl.testToolchain
import baaahs.model.*
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.scene.MutableModel
import baaahs.scene.MutableScene
import baaahs.scene.Scene
import baaahs.scene.mutable.SceneBuilder
import baaahs.show.Shader
import baaahs.show.Stream
import baaahs.show.live.LinkedPatch
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import baaahs.util.Clock
import baaahs.util.asInstant
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.scopes.TestWithConfigBuilder
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

fun <T> serializationRoundTrip(serializer: KSerializer<T>, obj: T): T {
    val json = testPlugins().json
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

fun lightBarForTest(
    name: String,
    position: Vector3F = Vector3F.origin,
    rotation: EulerAngle = EulerAngle.identity,
    scale: Vector3F = Vector3F.unit3d
) = LightBarData(
    name, startVertex = Vector3F.origin, endVertex = Vector3F.unit3d,
    position = position, rotation = rotation, scale = scale
)

fun entityDataForTest(
    name: String,
    position: Vector3F = Vector3F.origin,
    rotation: EulerAngle = EulerAngle.identity,
    scale: Vector3F = Vector3F.unit3d
): EntityData {
    val entityBuilders: List<() -> EntityData> = listOf(
        { GridData(name, rows = 2, columns = 2, rowGap = 1f, columnGap = 1f,
            position = position, rotation = rotation, scale = scale) },
        { LightRingData(name, position = position, rotation = rotation, scale = scale) },
        { LightBarData(name, startVertex = Vector3F.origin, endVertex = Vector3F.unit3d,
            position = position, rotation = rotation, scale = scale) },
    )
    return entityBuilders.random().invoke()
}

@Deprecated("Use sceneDataForTest().", ReplaceWith("sceneDataForTest(entities).model"))
fun fakeModel(vararg entities: Model.Entity) = modelForTest(entities.toList())
@Deprecated("Use sceneDataForTest().", ReplaceWith("sceneDataForTest(entities).model"))
fun fakeModel(entities: List<Model.Entity>) = modelForTest(entities)

val TestModelSurfaceData = testModelSurfaceData("Panel")
val TestSceneData = Scene(
    ModelData("Test Model", listOf("panel1")),
    mapOf("panel1" to TestModelSurfaceData),
    emptyMap(),
    emptyMap()
)
val TestModel = TestSceneData.open().model

@Deprecated("Use sceneDataForTest().", ReplaceWith("sceneDataForTest(entities).model"))
fun modelForTest(entities: List<Model.Entity>) = Model("Test Model", entities)
@Deprecated("Use sceneDataForTest().", ReplaceWith("sceneDataForTest(entities).model"))
fun modelForTest(vararg entities: Model.Entity) = Model("Test Model", entities.toList())

fun sceneDataForTest(vararg entities: EntityData, callback: MutableScene.() -> Unit = {}) =
    sceneDataForTest(entities.toList(), callback)

fun sceneDataForTest(entities: List<EntityData>, callback: MutableScene.() -> Unit = {}) =
    MutableScene(
        MutableModel("Test Model", entities.map { it.edit() }.toMutableList(), ModelUnit.Centimeters, 0f),
        mutableMapOf(),
        mutableMapOf()
    ).apply(callback).build(SceneBuilder())

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

val focused = NamedTag("Focused")
suspend fun TestWithConfigBuilder.focused(test: suspend io.kotest.core.test.TestScope.() -> kotlin.Unit): Unit =
    config(tags = setOf(focused), test = test)