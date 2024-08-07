package baaahs.app.ui

import baaahs.doc.FileType
import baaahs.plugin.Plugins
import baaahs.scene.Scene
import baaahs.scene.migration.SceneMigrator
import baaahs.show.Show
import baaahs.show.migration.ShowMigrator
import baaahs.util.encodeURIComponent
import js.objects.jso
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.w3c.files.Blob
import web.html.HTMLAnchorElement
import web.navigator.navigator
import web.window.WindowTarget

actual object UiActions {
    actual fun downloadShow(show: Show, plugins: Plugins) {
        val type = FileType.Show
        val filename = "${show.title}${type.extension}"
        val contentType = "application/json;charset=utf-8;"
        doDownload(filename, show, ShowMigrator.Migrate(filename), contentType, plugins)
    }

    actual fun downloadScene(scene: Scene, plugins: Plugins) {
        val type = FileType.Scene
        val filename = "${scene.title}${type.extension}"
        val contentType = "application/json;charset=utf-8;"
        doDownload(filename, scene, SceneMigrator.Migrate(filename), contentType, plugins)
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
        if (navigator.asDynamic()?.msSaveOrOpenBlob != null) {
            val blob = Blob(arrayOf(docJson), jso { this.type = contentType })
            navigator.asDynamic().msSaveOrOpenBlob(blob, filename)
        } else {
            val a = baaahs.document.createElement("a") as HTMLAnchorElement
            a.download = filename
            a.href = "data:${contentType},${encodeURIComponent(docJson)}"
            a.target = WindowTarget._blank
            baaahs.document.body.appendChild(a)
            a.click()
            baaahs.document.body.removeChild(a)
        }
    }
}