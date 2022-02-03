package baaahs.sm.brain.proto

import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.sm.brain.BrainId

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
    PING,
    USE_FIRMWARE;

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
        Type.USE_FIRMWARE -> UseFirmwareMessage.parse(reader)
    }
}

class BrainHelloMessage(val brainId: String, val surfaceName: String?, val firmwareVersion: String? = null,
                        val idfVersion: String? = null) : Message(Type.BRAIN_HELLO) {
    companion object {
        fun parse(reader: ByteArrayReader): BrainHelloMessage {
            val brainId = reader.readString()
            val surfaceName = reader.readNullableString()
            val firmwareVersion = if (reader.hasMoreBytes()) reader.readNullableString() else null
            val idfVersion = if (reader.hasMoreBytes()) reader.readNullableString() else null
            return BrainHelloMessage(brainId, surfaceName, firmwareVersion, idfVersion)
        }
    }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeString(brainId)
        writer.writeNullableString(surfaceName)
        writer.writeNullableString(firmwareVersion)
        writer.writeNullableString(idfVersion)
    }

    override fun toString(): String {
        return "BrainHello $brainId, $surfaceName, $firmwareVersion, $idfVersion"
    }
}

class BrainShaderMessage(val brainShader: BrainShader<*>, val buffer: BrainShader.Buffer, val pongData: ByteArray? = null) :
    Message(Type.BRAIN_PANEL_SHADE) {
    companion object {
        /**
         * Suboptimal parser; on the Brain we'll do better than this.
         */
        fun parse(reader: ByteArrayReader): BrainShaderMessage {
            val pongData = if (reader.readBoolean()) reader.readBytesWithSize() else null
            val shaderDesc = reader.readBytesWithSize()
            val shader = BrainShader.parse(ByteArrayReader(shaderDesc))
            val buffer = shader.readBuffer(reader)
            return BrainShaderMessage(shader, buffer, pongData)
        }
    }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeBoolean(pongData != null)
        if (pongData != null) writer.writeBytesWithSize(pongData)
        writer.writeBytesWithSize(brainShader.descriptorBytes)
        buffer.serialize(writer)
    }
}

/**
 * The message that Pinky will send to a brain when Pinky has decided that
 * the Brain should use a particular firmware. The url can point anywhere,
 * either self hosted by Pinky or out into the nether reaches of the Interwebs.
 * What could possibly go wrong? Pinky would __never__ tell a brain to go
 * download a wikipedia article and use that as a firmware. It just won't
 * be nice.
 */
class UseFirmwareMessage(val url: String) :
    Message(Type.USE_FIRMWARE) {
    companion object {
        fun parse(reader: ByteArrayReader): UseFirmwareMessage {
            return UseFirmwareMessage(reader.readString())
        }
    }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeString(url)
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
    val fixtureName: String?,
    val uvMapName: String?,
    val panelUvTopLeft: Vector2F,
    val panelUvBottomRight: Vector2F,
    val pixelCount: Int,
    val pixelLocations: List<Vector3F>
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

        private fun ByteArrayReader.readRelativeVerticesList(): List<Vector3F> {
            val vertexCount = readInt()
            return (0 until vertexCount).map {
                Vector3F(readFloat(), readFloat(), readFloat())
            }
        }

        private fun ByteArrayWriter.writeRelativeVerticesList(pixelLocations: List<Vector3F>) {
            writeInt(pixelLocations.size)
            pixelLocations.forEach { vertex ->
                writeFloat(vertex.x)
                writeFloat(vertex.y)
                writeFloat(vertex.z)
            }
        }
    }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeString(brainId.uuid)
        writer.writeNullableString(fixtureName)
        writer.writeNullableString(uvMapName)
        writer.writeVector2F(panelUvTopLeft)
        writer.writeVector2F(panelUvBottomRight)
        writer.writeInt(pixelCount)

        val vertexCount = pixelLocations.size
        writer.writeInt(vertexCount)
        writer.writeRelativeVerticesList(pixelLocations)
    }
}

class PingMessage(val data: ByteArray, val isPong: Boolean = false) : Message(Type.PING) {
    companion object {
        fun parse(reader: ByteArrayReader): PingMessage {
            val isPong = reader.readBoolean()
            val data = reader.readBytesWithSize()
            return PingMessage(data, isPong)
        }
    }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeBoolean(isPong)
        writer.writeBytesWithSize(data)
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

    open fun size(): Int = 8192
}
