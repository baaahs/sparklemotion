package baaahs

import baaahs.net.FragmentingUdpLink
import baaahs.net.TestNetwork
import baaahs.proto.BrainHelloMessage
import baaahs.shaders.SolidShader
import baaahs.sim.FakeDmxUniverse
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.test.Test
import kotlin.test.expect

@InternalCoroutinesApi
class PinkyTest {
    val network = TestNetwork(1_000_000)
    private val clientAddress = network.link()

    val sheepModel = SheepModel().apply { load() }
    val testShow1 = TestShow1()
    val pinky = Pinky(sheepModel, listOf(testShow1), network, FakeDmxUniverse(), StubPinkyDisplay())

    @Test
    fun asBrainsComeOnline_showShouldBeNotified() {
        pinky.receive(clientAddress.myAddress, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()

        val show = testShow1.createdShows.only()
        expect(show.shaderBuffers.size) { 1 }
        expect(show.shaderBuffers.keys.only() is Pinky.UnknownSurface) { true }

        pinky.receive(clientAddress.myAddress, BrainHelloMessage("brain1", "17L").toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()
        expect(show.shaderBuffers.size) { 1 }
        val surface = show.shaderBuffers.keys.only()
        expect(surface is SheepModel.Panel) { true }
        expect((surface as SheepModel.Panel).name) { "17L" }
    }

    fun defrag(bytes: ByteArray) = bytes.slice(FragmentingUdpLink.headerSize until bytes.size).toByteArray()

    class TestShow1(var supportsSurfaceChange: Boolean = true) : Show("TestShow1") {
        val createdShows = mutableListOf<ShowRenderer>()
        val solidShader = SolidShader()

        override fun createRenderer(sheepModel: SheepModel, showRunner: ShowRunner): Renderer {
            return ShowRenderer(showRunner).also { createdShows.add(it) }
        }

        inner class ShowRenderer(private val showRunner: ShowRunner) : Renderer {
            val shaderBuffers =
                showRunner.allSurfaces.associateWith { showRunner.getShaderBuffer(it, solidShader) }.toMutableMap()

            override fun nextFrame() {
                shaderBuffers.values.forEach { it.color = Color.WHITE }
            }

            override fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>) {
                if (!supportsSurfaceChange) {
                    super.surfacesChanged(newSurfaces, removedSurfaces)
                } else {
                    removedSurfaces.forEach { shaderBuffers.remove(it) }
                    newSurfaces.forEach { shaderBuffers[it] = showRunner.getShaderBuffer(it, solidShader) }
                }
            }
        }
    }

}