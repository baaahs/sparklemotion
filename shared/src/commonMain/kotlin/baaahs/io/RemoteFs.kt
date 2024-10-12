package baaahs.io

import baaahs.rpc.RpcImpl
import baaahs.rpc.Service
import baaahs.sim.FakeFs
import baaahs.sim.MergedFs
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass

@Serializable
data class RemoteFs(
    override val name: String,
    internal val fsId: Int,
    private val remoteFsBackend: RemoteFsBackend
) : Fs by remoteFsBackend

class FsServerSideSerializer : KSerializer<Fs>, RemoteFsSerializer {
    private val fses = mutableListOf<Fs>()

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("baaahs.io.Fs") {
            element("name", String.serializer().descriptor)
            element("fsId", Int.serializer().descriptor)
        }

    override fun deserialize(decoder: Decoder): Fs {
        return decoder.decodeStructure(descriptor) {
            var fsId: Int? = null
            loop@ while (true) {
                when (val i = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> decodeStringElement(descriptor, 0) // ignored
                    1 -> fsId = decodeIntElement(descriptor, 1)
                    else -> throw SerializationException("Unknown index $i")
                }
            }

            fses[fsId ?: throw MissingFieldException("fsId")]
        }
    }

    override fun serialize(encoder: Encoder, value: Fs) {
        var fsId = fses.indexOf(value)
        if (fsId == -1) {
            fsId = fses.size
            fses.add(value)
        }

        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeIntElement(descriptor, 1, fsId)
        }
    }

    override val serialModule: SerializersModule = SerializersModule {
        knownFsClasses.forEach {
            @Suppress("UNCHECKED_CAST")
            contextual(it as KClass<Fs>, this@FsServerSideSerializer)
        }
    }
}

abstract class FsClientSideSerializer : KSerializer<RemoteFs>, RemoteFsSerializer {
    abstract val backend: RemoteFsBackend

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("baaahs.io.Fs") {
            element("name", String.serializer().descriptor)
            element("fsId", Int.serializer().descriptor)
        }

    override fun deserialize(decoder: Decoder): RemoteFs {
        return decoder.decodeStructure(descriptor) {
            var name: String? = null
            var fsId: Int? = null
            loop@ while (true) {
                when (val i = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> name = decodeStringElement(descriptor, 0)
                    1 -> fsId = decodeIntElement(descriptor, 1)
                    else -> throw SerializationException("Unknown index $i")
                }
            }
            RemoteFs(
                name ?: throw MissingFieldException("name"),
                fsId ?: throw MissingFieldException("fsId"),
                backend
            )
        }
    }

    override fun serialize(encoder: Encoder, value: RemoteFs) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeIntElement(descriptor, 1, value.fsId)
        }
    }

    override val serialModule: SerializersModule = SerializersModule {
        knownFsClasses.forEach {
            @Suppress("UNCHECKED_CAST")
            contextual(it as KClass<RemoteFs>, this@FsClientSideSerializer)
        }
    }
}

internal class MissingFieldException(fieldName: String) :
    SerializationException("Field '$fieldName' is required, but it was missing")

@Service
interface RemoteFsCommands {
    suspend fun listFiles(directory: Fs.File): List<Fs.File>
    suspend fun loadFile(file: Fs.File): String?
    suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean)
    suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean)
    suspend fun exists(file: Fs.File): Boolean
    suspend fun isDirectory(file: Fs.File): Boolean
    suspend fun renameFile(fromFile: Fs.File, toFile: Fs.File)
    suspend fun delete(file: Fs.File)

    companion object
}

interface RemoteFsSerializer {
    val serialModule: SerializersModule

    @Suppress("UNCHECKED_CAST")
    val asSerializer: KSerializer<Fs>
        get() = this as KSerializer<Fs>

    fun createRpcImpl(): RpcImpl<RemoteFsCommands> {
        return RemoteFsCommands.getImpl(
            "pinky/remoteFs",
            SerializersModule {
                include(serialModule)
            }
        )
    }
}

interface RemoteFsBackend : Fs

expect val platformFsClasses: Set<KClass<out Fs>>
val knownFsClasses: Set<KClass<out Fs>>
    get() = platformFsClasses + setOf(Fs::class, FakeFs::class, MergedFs::class, RemoteFs::class)
