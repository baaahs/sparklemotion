package baaahs.app.ui

import baaahs.doc.FileType
import baaahs.plugin.Plugins
import baaahs.scene.Scene
import baaahs.show.SceneMigrator
import baaahs.show.Show
import baaahs.show.ShowMigrator
import baaahs.util.encodeURIComponent
import baaahs.window
import kotlinext.js.jsObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLAnchorElement
import org.w3c.files.Blob

object UiActions {
    fun downloadShow(show: Show, plugins: Plugins) {
        val type = FileType.Show
        val filename = "${show.title}${type.extension}"
        val contentType = "application/json;charset=utf-8;"
        doDownload(filename, show, ShowMigrator, contentType, plugins)
    }

    fun downloadScene(scene: Scene, plugins: Plugins) {
        val type = FileType.Scene
        val filename = "${scene.title}${type.extension}"
        val contentType = "application/json;charset=utf-8;"
        doDownload(filename, scene, SceneMigrator, contentType, plugins)
    }

    private fun <T: Any> doDownload(
        filename: String,
        document: T,
        serializer: KSerializer<T>,
        contentType: String,
        plugins: Plugins
    ) {
        val json = Json(plugins.json) { prettyPrint = true }
        val docJson = json.encodeToString(serializer, document)
        val navigator = window.navigator
        if (navigator.asDynamic()?.msSaveOrOpenBlob != null) {
            val blob = Blob(arrayOf(docJson), jsObject { this.type = contentType })
            navigator.asDynamic().msSaveOrOpenBlob(blob, filename);
        } else {
            val a = baaahs.document.createElement("a") as HTMLAnchorElement
            a.download = filename;
            a.href = "data:${contentType},${encodeURIComponent(docJson)}"
            a.target = "_blank"
            baaahs.document.body!!.appendChild(a)
            a.click()
            baaahs.document.body!!.removeChild(a)
        }
    }
}