package baaahs

import baaahs.fixtures.NullTransport
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.mapper.MappingResults
import baaahs.proto.BrainHelloMessage
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import mockk
import org.spekframework.spek2.Spek
import kotlin.random.Random

object BrainManagerSpec : Spek({
    describe<BrainManager> {
        val resultsByBrainId by value { mutableMapOf<BrainId, MappingResults.Info>() }
        val resultsBySurfaceName by value { mutableMapOf<String, MappingResults.Info>() }
        val mappingResults by value { FakeMappingResults(resultsByBrainId, resultsBySurfaceName) }

        val brainManager by value {
            BrainManager(
                mockk(), PermissiveFirmwareDaddy(), TestModel, mappingResults,
                mockk(), Pinky.NetworkStats(), FakeClock(), StubPubSubServer(), LinearSurfacePixelStrategy(Random(1))
            )
        }

        context("#createFixtureFor") {
            val brainId by value { "brain1" }
            val msgSurfaceName by value<String?> { null }
            val surface by value { TestModelSurface("surface1", 2, vertices = listOf(
                Vector3F(1f, 1f, 1f),
                Vector3F(2f, 2f, 1f),
                Vector3F(1f, 2f, 2f),
                Vector3F(2f, 1f, 2f)
            )) }
            val pixelLocations by value<List<Vector3F?>?> { null }
            val mappingInfo by value { MappingResults.Info(surface, pixelLocations) }
            val brainHelloMessage by value { BrainHelloMessage(brainId, msgSurfaceName, null, null) }

            val subject by value { brainManager.createFixtureFor(brainHelloMessage, NullTransport) }

            context("when the brain id is mapped to a model element") {
                override(resultsByBrainId) { mapOf(BrainId(brainId) to mappingInfo) }

                it("should create a fixture") {
                    expect(subject.modelEntity).toBe(surface)
                    expect(subject.pixelCount).toBe(2)
                    expect(subject.pixelLocations).toBe(LinearSurfacePixelStrategy(Random(1)).forKnownSurface(2, surface, TestModel))
                }
            }

            context("when the surface name is specified") {
                override(msgSurfaceName) { "surface1" }
                override(resultsBySurfaceName) { mapOf(msgSurfaceName to mappingInfo) }

                it("should create a fixture") {
                    expect(subject.modelEntity).toBe(surface)
                    expect(subject.pixelCount).toBe(2)
                    expect(subject.pixelLocations).toBe(LinearSurfacePixelStrategy(Random(1)).forKnownSurface(2, surface, TestModel))
                }
            }
        }
    }
})

class FakeMappingResults(
    resultsByBrainId: Map<BrainId, MappingResults.Info> = mapOf(),
    resultsBySurfaceName: Map<String, MappingResults.Info> = mapOf()
) : MappingResults {
    val resultsByBrainId = resultsByBrainId.toMutableMap()
    val resultsBySurfaceName = resultsBySurfaceName.toMutableMap()

    override fun dataFor(brainId: BrainId) = resultsByBrainId[brainId]
    override fun dataFor(surfaceName: String) = resultsBySurfaceName[surfaceName]
}