package baaahs.proto

import baaahs.BrainId
import baaahs.Shader
import baaahs.geom.Vector2F
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

object Ports {
    const val PINKY = 8002
    const val BRAIN = 8003

    const val PINKY_UI_TCP = 8004
    const val PINKY_MAPPER_TCP = 8005
    const val SIMULATOR_BRIDGE_TCP = 8006
}

enum class Type {
    // UDP:
    BRAIN_HELLO,
    BRAIN_PANEL_SHADE,
    MAPPER_HELLO,
    BRAIN_ID_REQUEST,
    BRAIN_MAPPING,
    PING;

    companion object {
        val values = values()
        fun get(i: Byte) = values[i.toInt()]
    }
}

fun parse(bytes: ByteArray): Message {
    val reader = ByteArrayReader(bytes)
    return when (Type.get(reader.readByte())) {
        Type.BRAIN_HELLO -> BrainHelloMessage.parse(reader)
        Type.BRAIN_PANEL_SHADE -> BrainShaderMessage.parse(reader)
        Type.MAPPER_HELLO -> MapperHelloMessage.parse(reader)
        Type.BRAIN_ID_REQUEST -> BrainIdRequest.parse(reader)
        Type.BRAIN_MAPPING -> BrainMappingMessage.parse(reader)
        Type.PING -> PingMessage.parse(reader)
    }
}

class BrainHelloMessage(val brainId: String, val surfaceName: String?) : Message(Type.BRAIN_HELLO) {
    companion object {
        fun parse(reader: ByteArrayReader): BrainHelloMessage {
            return BrainHelloMessage(
                reader.readString(),
                reader.readNullableString()
            )
        }
    }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeString(brainId)
        writer.writeNullableString(surfaceName)
    }
}

class BrainShaderMessage(val shader: Shader<*>, val buffer: Shader.Buffer, val pongData: ByteArray? = null) :
    Message(Type.BRAIN_PANEL_SHADE) {
    companion object {
        /**
         * Suboptimal parser; on the Brain we'll do better than this.
         */
        fun parse(reader: ByteArrayReader): BrainShaderMessage {
            val pongData = if (reader.readBoolean()) reader.readBytes() else null
            val shaderDesc = reader.readBytes()
            val shader = Shader.parse(ByteArrayReader(shaderDesc))
            val buffer = shader.readBuffer(reader)
            return BrainShaderMessage(shader, buffer, pongData)
        }
    }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeBoolean(pongData != null)
        if (pongData != null) writer.writeBytes(pongData)
        writer.writeBytes(shader.descriptorBytes)
        buffer.serialize(writer)
    }
}

class MapperHelloMessage(val isRunning: Boolean) : Message(Type.MAPPER_HELLO) {
    companion object {
        fun parse(reader: ByteArrayReader): MapperHelloMessage {
            return MapperHelloMessage(reader.readBoolean())
        }
    }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeBoolean(isRunning)
    }
}

class BrainIdRequest : Message(Type.BRAIN_ID_REQUEST) {
    companion object {
        fun parse(reader: ByteArrayReader) = BrainIdRequest()
    }

    override fun serialize(writer: ByteArrayWriter) {
    }
}

class BrainMappingMessage(
    val brainId: BrainId,
    val surfaceName: String?,
    val uvMapName: String?,
    val panelUvTopLeft: Vector2F,
    val panelUvBottomRight: Vector2F,
    val pixelCount: Int,
    val pixelVertices: List<Vector2F>
) : Message(Type.BRAIN_MAPPING) {

    companion object {
        fun parse(reader: ByteArrayReader) = BrainMappingMessage(
            BrainId(reader.readString()), // brainId
            reader.readNullableString(), // surfaceName
            reader.readNullableString(), // uvMapName
            reader.readVector2F(), // panelUvTopLeft
            reader.readVector2F(), // panelUvBottomRight
            reader.readInt(), // pixelCount
            reader.readRelativeVerticesList()
        )

        private fun ByteArrayReader.readVector2F() = Vector2F(readFloat(), readFloat())

        private fun ByteArrayWriter.writeVector2F(v: Vector2F) {
            writeFloat(v.x)
            writeFloat(v.y)
        }

        private fun ByteArrayReader.readRelativeVerticesList(): List<Vector2F> {
            val vertexCount = readInt()
            return (0 until vertexCount).map {
                Vector2F(readShort() / 65536.0f, readShort() / 65536.0f)
            }
        }

        private fun ByteArrayWriter.writeRelativeVerticesList(pixelVertices: List<Vector2F>) {
            writeInt(pixelVertices.size)
            pixelVertices.forEach { vertex ->
                if (vertex.x < 0 || vertex.x > 1 || vertex.y < 0 || vertex.y > 1) {
//                    throw IllegalArgumentException("Pixel vertices must be [0..1], but $vertex!")
                }

                writeShort((vertex.x * 65536).toShort())
                writeShort((vertex.y * 65536).toShort())
            }
        }
    }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeString(brainId.uuid)
        writer.writeNullableString(surfaceName)
        writer.writeNullableString(uvMapName)
        writer.writeVector2F(panelUvTopLeft)
        writer.writeVector2F(panelUvBottomRight)
        writer.writeInt(pixelCount)

        val vertexCount = pixelVertices.size
        writer.writeInt(vertexCount)
        writer.writeRelativeVerticesList(pixelVertices)
    }
}

class PingMessage(val data: ByteArray, val isPong: Boolean = false) : Message(Type.PING) {
    companion object {
        fun parse(reader: ByteArrayReader): PingMessage {
            val isPong = reader.readBoolean()
            val data = reader.readBytes()
            return PingMessage(data, isPong)
        }
    }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeBoolean(isPong)
        writer.writeBytes(data)
    }
}

open class Message(val type: Type) {
    // TODO: send message length as the first four bytes, plus maybe sequence/reassembly info for UDP
    fun toBytes(): ByteArray {
        val writer = ByteArrayWriter(1 + size())
        writer.writeByte(type.ordinal.toByte())
        serialize(writer)
        return writer.toBytes()
    }

    open fun serialize(writer: ByteArrayWriter) {
    }

    open fun size(): Int = 127
}