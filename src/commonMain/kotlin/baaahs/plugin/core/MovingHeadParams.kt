package baaahs.plugin.core

import baaahs.dmx.Dmx
import baaahs.fixtures.Fixture
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.result.FloatsResultType
import baaahs.model.MovingHead
import baaahs.visualizer.remote.RemoteVisualizers
import com.danielgergely.kgl.GL_RGBA

data class MovingHeadParams(
    /** In radians. */
    val pan: Float,
    /** In radians. */
    val tilt: Float,
    val colorWheel: Float,
    val dimmer: Float
) {
    fun send(buffer: MovingHead.Buffer) {
        buffer.pan = pan
        buffer.tilt = tilt
        // TODO: Not all moving heads have a color wheel, some use RGB.
        buffer.colorWheelPosition = colorWheel
        buffer.dimmer = dimmer
    }

    companion object {
        val struct = GlslType.Struct(
            "MovingHeadParams",
            "pan" to GlslType.Float,
            "tilt" to GlslType.Float,
            "colorWheel" to GlslType.Float,
            "dimmer" to GlslType.Float,
            defaultInitializer = GlslExpr("MovingHeadParams(0., 0., 0., 1.)")
        )

        val contentType = ContentType(
            "moving-head-params", "Moving Head Params",
            struct, outputRepresentation = GlslType.Vec4
        )

        val resultType = object : FloatsResultType<ResultBuffer>(4, GL_RGBA) {
            override fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer =
                ResultBuffer(gl, index, this)
        }
    }

    class ResultBuffer(gl: GlContext, index: Int, type: FloatsResultType<ResultBuffer>) : FloatsResultType.Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): MovingHeadParams {
            val offset = pixelIndex * type.stride

            return MovingHeadParams(
                pan = floatBuffer[offset],
                tilt = floatBuffer[offset + 1],
                colorWheel = floatBuffer[offset + 2],
                dimmer = floatBuffer[offset + 3]
            )
        }

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): baaahs.gl.result.FixtureResults =
            FixtureResults(fixture, bufferOffset)

        inner class FixtureResults(
            private val fixture: Fixture,
            componentOffset: Int
        ) : baaahs.gl.result.FixtureResults(componentOffset, unitCount) {
            val movingHeadParams get() = this@ResultBuffer[componentOffset]

            private val movingHead = fixture.modelEntity as MovingHead
            private val adapter = movingHead.adapter
            private val channels = ByteArray(adapter.dmxChannelCount)
            private val movingHeadBuffer = adapter.newBuffer(Dmx.Buffer(channels))

            override fun send(remoteVisualizers: RemoteVisualizers) {
                movingHeadParams.send(movingHeadBuffer)
                fixture.transport.deliverBytes(channels)
                remoteVisualizers.sendFrameData(movingHead) { out ->
                    out.writeShort(channels.size)
                    out.writeBytes(channels)
                }
            }
        }
    }
}