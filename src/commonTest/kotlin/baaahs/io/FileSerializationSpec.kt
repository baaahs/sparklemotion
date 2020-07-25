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

        val fakeRemoteFsBackend by value {
            object : RemoteFsBackend {
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
        }

        val clientJson by value {
            val fsClientSideSerializer = object : FsClientSideSerializer() {
                override val backend: RemoteFsBackend
                    get() = fakeRemoteFsBackend

            }
            Json(JsonConfiguration.Stable, fsClientSideSerializer.serialModule)
        }

        val serverJson by value {
            val fsServerSideSerializer = FsServerSideSerializer()
            Json(JsonConfiguration.Stable, fsServerSideSerializer.serialModule)
        }

        context("server-side serializer") {
            it("sends Fs objects by id ref") {
                val actualFile1Json = serverJson.toJson(Fs.File.serializer(), actualFile1)
                expect(json {
                    "fs" to json { "name" to actualFile1.fs.name; "fsId" to 0 }
                    "pathParts" to jsonArray { +"some"; +"file.txt" }
                    "isDirectory" to JsonNull
                }) { actualFile1Json}

                val actualFile2Json = serverJson.toJson(Fs.File.serializer(), actualFile2)
                expect(json {
                    "fs" to json { "name" to actualFile2.fs.name; "fsId" to 1 }
                    "pathParts" to jsonArray { +"another"; +"file.txt" }
                    "isDirectory" to JsonNull
                }) { actualFile2Json}

                val thirdFile = actualFile1.parent!!.resolve("foo.txt")
                val thirdFileJson = serverJson.toJson(Fs.File.serializer(), thirdFile)
                expect(json {
                    "fs" to json { "name" to thirdFile.fs.name; "fsId" to 0 }
                    "pathParts" to jsonArray { +"some"; +"foo.txt" }
                    "isDirectory" to JsonNull
                }) { thirdFileJson}
            }
        }

        context("client-side serializer") {
            val testCoroutineContext by value { TestCoroutineContext("network") }

            it("converts all Fs types to RemoteFs") {
                val remoteFile1 = clientJson.fromJson(Fs.File.serializer(), json {
                    "fs" to json { "name" to "Name"; "fsId" to 0 }
                    "pathParts" to jsonArray { +"some"; +"file.txt" }
                    "isDirectory" to JsonNull
                })
                expect("some/file.txt") { remoteFile1.fullPath }

                val resultFiles = mutableListOf<Fs.File>()
                CoroutineScope(testCoroutineContext).launch {
                    resultFiles.addAll(remoteFile1.fs.listFiles(remoteFile1))
                }
                testCoroutineContext.runAll()

                expect(listOf("fake/response.txt")) { resultFiles.map { it.fullPath }}
            }
        }

        context("round trip") {
            val jsonFromServer by value { serverJson.stringify(Fs.File.serializer(), actualFile1) }
            val clientSideFile by value { clientJson.parse(Fs.File.serializer(), jsonFromServer) }
            val jsonFromClient by value { clientJson.stringify(Fs.File.serializer(), clientSideFile) }
            val serverSideFile by value { serverJson.parse(Fs.File.serializer(), jsonFromClient) }

            it("converts file to RemoteFs and back again to actual Fs") {
                expect(actualFile1.fullPath) { serverSideFile.fullPath }
                expect(actualFile1.fs) { serverSideFile.fs }
            }
        }
    }
})