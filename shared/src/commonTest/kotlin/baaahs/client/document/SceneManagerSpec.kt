package baaahs.client.document

import baaahs.DocumentState
import baaahs.TestRig
import baaahs.client.Notifier
import baaahs.describe
import baaahs.doc.FileType
import baaahs.gl.testPlugins
import baaahs.io.Fs
import baaahs.io.FsServerSideSerializer
import baaahs.io.PubSubRemoteFsClientBackend
import baaahs.scene.Scene
import baaahs.scene.SceneMonitor
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.isSameAs
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.coroutines.InternalCoroutinesApi
import org.spekframework.spek2.Spek

@OptIn(InternalCoroutinesApi::class)
object SceneManagerSpec : Spek({
    describe<SceneManager> {
        val pubSubRig by value { TestRig() }
        val plugins by value { testPlugins() }
        val sceneMonitor by value { SceneMonitor() }
        val sceneManager by value {
            SceneManager(
                pubSubRig.client1, PubSubRemoteFsClientBackend(pubSubRig.client1),
                plugins, Notifier(pubSubRig.client1), FakeFileDialog(), sceneMonitor
            )
        }
        val initialScene by value<Scene?> { null }
        val serverSceneChannel by value {
            val topic = Scene.createTopic(plugins.serialModule, FsServerSideSerializer())
            val documentState = initialScene?.let { DocumentState(it, Unit, false, null) }
            pubSubRig.server.publish(topic, documentState) { error("no!") }
        }

        beforeEachTest {
            serverSceneChannel.run {}
            sceneManager.run {}
        }

        it("starts off with no scene loaded") {
            expect(sceneManager.file).toBe(null)
            expect(sceneManager.document).toBe(null)
            expect(sceneManager.isUnsaved).toBe(false)
            expect(sceneManager.isLoaded).toBe(false)
            expect(sceneManager.everSynced).toBe(false)
        }

        context("when a scene is loaded") {
            val newScene by value { Scene.Empty }
            beforeEachTest {
                serverSceneChannel.onChange(DocumentState(newScene, Unit, false, null))
                pubSubRig.testCoroutineScope.advanceUntilIdle()
            }

            it("has a scene loaded") {
                expect(sceneManager.file).toBe(null)
                expect(sceneManager.document).toEqual(newScene)
                expect(sceneManager.isUnsaved).toBe(false)
                expect(sceneManager.isLoaded).toBe(true)
                expect(sceneManager.everSynced).toBe(true)
            }

            context("while editing the scene") {
                val mutableScene by value { sceneManager.facade.mutableScene }

                it("creates a mutable scene") {
                    expect(mutableScene.title).toEqual(newScene.title)
                }

                context("when an edit is made") {
                    beforeEachTest {
                        mutableScene.run {}
                        sceneManager.facade.onEdit()
                    }

                    it("should retain the same mutable scene") {
                        expect(mutableScene).isSameAs(sceneManager.facade.mutableScene)
                    }
                }
            }
        }
    }
})

class FakeFileDialog : IFileDialog {
    override suspend fun open(fileType: FileType, defaultFile: Fs.File?): Fs.File? =
        TODO("not implemented")
    override suspend fun saveAs(fileType: FileType, defaultFile: Fs.File?, defaultFileName: String?): Fs.File? =
        TODO("not implemented")
    override suspend fun onSelect(file: Fs.File): Unit =
        TODO("not implemented")
    override suspend fun onCancel(): Unit =
        TODO("not implemented")

}
