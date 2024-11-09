package baaahs.io

import baaahs.kotest.value
import baaahs.sim.FakeFs
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import ext.kotlinx_coroutines_test.TestCoroutineDispatcher
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*

@InternalCoroutinesApi
object FileSerializationSpec : DescribeSpec({
    describe("Remote file serialization") {
        val actualFs1 by value { FakeFs() }
        val actualFile1 by value { actualFs1.resolve("some", "file.txt") }

        val actualFs2 by value { FakeFs() }
        val actualFile2 by value { actualFs2.resolve("another", "file.txt") }

        val fakeRemoteFsBackend by value { FakeRemoteFsBackend() }

        val clientJson by value {
            val fsClientSideSerializer = object : FsClientSideSerializer() {
                override val backend: RemoteFsBackend
                    get() = fakeRemoteFsBackend

            }
            Json { serializersModule = fsClientSideSerializer.serialModule }
        }

        val serverJson by value {
            val fsServerSideSerializer = FsServerSideSerializer()
            Json {
                encodeDefaults = true
                serializersModule = fsServerSideSerializer.serialModule
            }
        }

        context("server-side serializer") {
            it("sends Fs objects by id ref") {
                val actualFile1Json = serverJson.encodeToJsonElement(Fs.File.serializer(), actualFile1)
                expect(actualFile1Json).toBe(buildJsonObject {
                    put("fs", buildJsonObject { put("name", actualFile1.fs.name); put("fsId", 0) })
                    put("pathParts", buildJsonArray { add("some"); add("file.txt") })
                    put("isDirectory", JsonNull)
                })

                val actualFile2Json = serverJson.encodeToJsonElement(Fs.File.serializer(), actualFile2)
                expect(actualFile2Json).toBe(buildJsonObject {
                    put("fs", buildJsonObject { put("name", actualFile2.fs.name); put("fsId", 1) })
                    put("pathParts", buildJsonArray { add("another"); add("file.txt") })
                    put("isDirectory", JsonNull)
                })

                val thirdFile = actualFile1.parent!!.resolve("foo.txt")
                val thirdFileJson = serverJson.encodeToJsonElement(Fs.File.serializer(), thirdFile)
                expect(thirdFileJson).toBe(buildJsonObject {
                    put("fs", buildJsonObject { put("name", thirdFile.fs.name); put("fsId", 0) })
                    put("pathParts", buildJsonArray { add("some"); add("foo.txt") })
                    put("isDirectory", JsonNull)
                })
            }
        }

        context("client-side serializer") {
            val dispatcher by value { TestCoroutineDispatcher() }

            it("converts all Fs types to RemoteFs") {
                val remoteFile1 = clientJson.decodeFromJsonElement(Fs.File.serializer(), buildJsonObject {
                    put("fs", buildJsonObject { put("name", "Name"); put("fsId", 0) })
                    put("pathParts", buildJsonArray { add("some"); add("file.txt") })
                    put("isDirectory", JsonNull)
                })
                expect(remoteFile1.fullPath).toBe("some/file.txt")

                val resultFiles = mutableListOf<Fs.File>()
                CoroutineScope(dispatcher).launch {
                    resultFiles.addAll(remoteFile1.fs.listFiles(remoteFile1))
                }
                dispatcher.runCurrent()

                expect(resultFiles.map { it.fullPath }).containsExactly("fake/response.txt")
            }
        }

        context("round trip") {
            val jsonFromServer by value { serverJson.encodeToString(Fs.File.serializer(), actualFile1) }
            val clientSideFile by value { clientJson.decodeFromString(Fs.File.serializer(), jsonFromServer) }
            val jsonFromClient by value { clientJson.encodeToString(Fs.File.serializer(), clientSideFile) }
            val serverSideFile by value { serverJson.decodeFromString(Fs.File.serializer(), jsonFromClient) }

            it("converts file to RemoteFs and back again to actual Fs") {
                expect(serverSideFile.fullPath).toBe(actualFile1.fullPath)
                expect(serverSideFile.fs).toBe(actualFile1.fs)
            }
        }
    }
})

class FakeRemoteFsBackend : RemoteFsBackend {
    override val name: String
        get() = "Remote Fs"

    override suspend fun listFiles(directory: Fs.File): List<Fs.File> {
        return listOf(Fs.File(this, "fake/response.txt", false))
    }

    override suspend fun loadFile(file: Fs.File): String? {
        TODO("not implemented")
    }

    override suspend fun saveFile(file: Fs.File, content: ByteArray, allowOverwrite: Boolean) {
        TODO("not implemented")
    }

    override suspend fun saveFile(file: Fs.File, content: String, allowOverwrite: Boolean) {
        TODO("not implemented")
    }

    override suspend fun exists(file: Fs.File): Boolean {
        TODO("not implemented")
    }

    override suspend fun renameFile(fromFile: Fs.File, toFile: Fs.File) {
        TODO("not implemented")
    }

    override suspend fun delete(file: Fs.File) {
        TODO("not implemented")
    }
}