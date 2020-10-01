package baaahs.io

import baaahs.sim.FakeFs
import ext.TestCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

@InternalCoroutinesApi
object FileSerializationSpec : Spek({
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
            Json { serializersModule = fsServerSideSerializer.serialModule }
        }

        context("server-side serializer") {
            it("sends Fs objects by id ref") {
                val actualFile1Json = serverJson.encodeToJsonElement(Fs.File.serializer(), actualFile1)
                expect(buildJsonObject {
                    put("fs", buildJsonObject { put("name", actualFile1.fs.name); put("fsId", 0) })
                    put("pathParts", buildJsonArray { add("some"); add("file.txt") })
                    put("isDirectory", JsonNull)
                }) { actualFile1Json }

                val actualFile2Json = serverJson.encodeToJsonElement(Fs.File.serializer(), actualFile2)
                expect(buildJsonObject {
                    put("fs", buildJsonObject { put("name", actualFile2.fs.name); put("fsId", 1) })
                    put("pathParts", buildJsonArray { add("another"); add("file.txt") })
                    put("isDirectory", JsonNull)
                }) { actualFile2Json }

                val thirdFile = actualFile1.parent!!.resolve("foo.txt")
                val thirdFileJson = serverJson.encodeToJsonElement(Fs.File.serializer(), thirdFile)
                expect(buildJsonObject {
                    put("fs", buildJsonObject { put("name", thirdFile.fs.name); put("fsId", 0) })
                    put("pathParts", buildJsonArray { add("some"); add("foo.txt") })
                    put("isDirectory", JsonNull)
                }) { thirdFileJson }
            }
        }

        context("client-side serializer") {
            val testCoroutineContext by value { TestCoroutineContext("network") }

            it("converts all Fs types to RemoteFs") {
                val remoteFile1 = clientJson.decodeFromJsonElement(Fs.File.serializer(), buildJsonObject {
                    put("fs", buildJsonObject { put("name", "Name"); put("fsId", 0) })
                    put("pathParts", buildJsonArray { add("some"); add("file.txt") })
                    put("isDirectory", JsonNull)
                })
                expect("some/file.txt") { remoteFile1.fullPath }

                val resultFiles = mutableListOf<Fs.File>()
                CoroutineScope(testCoroutineContext).launch {
                    resultFiles.addAll(remoteFile1.fs.listFiles(remoteFile1))
                }
                testCoroutineContext.runAll()

                expect(listOf("fake/response.txt")) { resultFiles.map { it.fullPath } }
            }
        }

        context("round trip") {
            val jsonFromServer by value { serverJson.encodeToString(Fs.File.serializer(), actualFile1) }
            val clientSideFile by value { clientJson.decodeFromString(Fs.File.serializer(), jsonFromServer) }
            val jsonFromClient by value { clientJson.encodeToString(Fs.File.serializer(), clientSideFile) }
            val serverSideFile by value { serverJson.decodeFromString(Fs.File.serializer(), jsonFromClient) }

            it("converts file to RemoteFs and back again to actual Fs") {
                expect(actualFile1.fullPath) { serverSideFile.fullPath }
                expect(actualFile1.fs) { serverSideFile.fs }
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
}