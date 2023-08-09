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
    val dimmer: Float,
    val prism: Boolean,
    val prismRotation: Float,
) {
    fun send(buffer: MovingHead.Buffer) {
        buffer.pan = pan
        buffer.tilt = tilt
        // TODO: Not all moving heads have a color wheel, some use RGB.
        buffer.colorWheelPosition = colorWheel
        buffer.dimmer = dimmer
        buffer.prism = prism
        buffer.prismRotation = prismRotation
    }

    companion object {
        val struct = GlslType.Struct(
            "MovingHeadParams",
            "pan" to GlslType.Float,
            "tilt" to GlslType.Float,
            "colorWheel" to GlslType.Float,
            "dimmer" to GlslType.Float,
            "prism" to GlslType.Bool,
            "prismRotation" to GlslType.Float,
            defaultInitializer = GlslExpr("MovingHeadParams(0., 0., 0., 1., false, 0.)"),
            outputOverride = { varName: String ->
                """
                    vec4(
                        ${varName}.pan,
                        ${varName}.tilt,
                        ${varName}.colorWheel,
                        //  prism bit | 1 bit sign(prismRotation) | 14 bit magnitude(prismRotation) | 16 bit unsigned fixed-point dimmer
                        uintBitsToFloat((uint (${varName}.dimmer * 65535.)) & 0xFFFFu
                            | ($varName.prismRotation < 0. ? 1u : 0u) << 30
                            | ((uint (abs($varName.prismRotation) * 16384.)) & 0x3FFFu) << 16
                            | ((${varName}.prism ? 1u : 0u) << 31))
                    )
                """.trimIndent()
            }
        )

        val contentType = ContentType(
            "moving-head-params", "Moving Head Params",
            struct, outputRepresentation = GlslType.Vec4, defaultInitializer = { _ ->
                struct.defaultInitializer
            }
        )

        val resultType = object : FloatsResultType<ResultBuffer>(4, GL_RGBA) {
            override fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer =
                ResultBuffer(gl, index, this)
        }
    }

    class ResultBuffer(gl: GlContext, index: Int, type: FloatsResultType<ResultBuffer>) : FloatsResultType.Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): MovingHeadParams {
            val offset = pixelIndex * type.stride

            val packedDimmerAndPrism = floatBuffer[offset+3].toRawBits();
            val prism : Boolean = (packedDimmerAndPrism.and(1 shl 31) == 1 shl 31)
            val dimmer = (packedDimmerAndPrism.and(0xFFFF)).toFloat() / 65535f;

            val prismRotationSign = packedDimmerAndPrism.shr(30).and(0x1) != 0
            val prismRotationMagnitude = packedDimmerAndPrism.shr(16).and(0x3FFF).toFloat().div(16384)
            val prismRotation = prismRotationMagnitude * (if (prismRotationSign) {
                -1f
            } else {
                1f
            })

            return MovingHeadParams(
                pan = floatBuffer[offset],
                tilt = floatBuffer[offset + 1],
                colorWheel = floatBuffer[offset + 2],
                dimmer = dimmer,
                prism = prism,
                prismRotation = prismRotation,
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