package baaahs.client.document

import baaahs.DocumentState
import baaahs.TestRig
import baaahs.app.settings.FeatureFlags
import baaahs.app.settings.ObservableProvider
import baaahs.client.Notifier
import baaahs.describe
import baaahs.doc.FileType
import baaahs.gl.testPlugins
import baaahs.io.Fs
import baaahs.io.FsServerSideSerializer
import baaahs.io.PubSubRemoteFsClientBackend
import baaahs.kotest.value
import baaahs.scene.Scene
import baaahs.scene.SceneMonitor
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class)
class SceneManagerSpec : DescribeSpec({
    describe<SceneManager> {
        val pubSubRig by value { TestRig() }
        val plugins by value { testPlugins() }
        val sceneMonitor by value { SceneMonitor() }
        val sceneManager by value {
            SceneManager(
                pubSubRig.client1, PubSubRemoteFsClientBackend(pubSubRig.client1),
                plugins, Notifier(pubSubRig.client1), FakeFileDialog(), sceneMonitor,
                ObservableProvider(FeatureFlags())
            )
        }
        val initialScene by value<Scene?> { null }
        val serverSceneChannel by value {
            val topic = Scene.createTopic(plugins.serialModule, FsServerSideSerializer())
            val documentState = initialScene?.let { DocumentState(it, Unit, false, null) }
            pubSubRig.server.publish(topic, documentState) { error("no!") }
        }

        beforeEach {
            serverSceneChannel.run {}
            sceneManager.run {}
        }

        it("starts off with no scene loaded") {
            sceneManager.file.shouldBe(null)
            sceneManager.document.shouldBe(null)
            sceneManager.isUnsaved.shouldBeFalse()
            sceneManager.isLoaded.shouldBeFalse()
            sceneManager.everSynced.shouldBeFalse()
        }

        context("when a scene is loaded") {
            val newScene by value { Scene.Empty }
            beforeEach {
                serverSceneChannel.onChange(DocumentState(newScene, Unit, false, null))
                pubSubRig.pinkyScope.advanceUntilIdle()
            }

            it("has a scene loaded") {
                sceneManager.file.shouldBe(null)
                sceneManager.document.shouldBe(newScene)
                sceneManager.isUnsaved.shouldBeFalse()
                sceneManager.isLoaded.shouldBeTrue()
                sceneManager.everSynced.shouldBeTrue()
            }

            context("while editing the scene") {
                val mutableScene by value { sceneManager.facade.mutableScene }

                it("creates a mutable scene") {
                    mutableScene.title.shouldBe(newScene.title)
                }

                context("when an edit is made") {
                    beforeEach {
                        mutableScene.run {}
                        sceneManager.facade.onEdit()
                    }

                    it("should retain the same mutable scene") {
                        mutableScene.shouldBeSameInstanceAs(sceneManager.facade.mutableScene)
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
