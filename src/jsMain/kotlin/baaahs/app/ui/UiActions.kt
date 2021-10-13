package baaahs.app.ui

import baaahs.document
import baaahs.plugin.Plugins
import baaahs.show.Show
import baaahs.show.ShowMigrator
import baaahs.window
import baaahs.util.encodeURIComponent
import kotlinext.js.jsObject
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLAnchorElement
import org.w3c.files.Blob

object UiActions {
    fun downloadShow(show: Show, plugins: Plugins) {
        val filename = "${show.title}.sparkle"
        val contentType = "application/json;charset=utf-8;"
        val showJson = Json(plugins.json) {
            prettyPrint = true
        }.encodeToString(ShowMigrator, show)
        val navigator = window.navigator
        if (navigator.asDynamic()?.msSaveOrOpenBlob != null) {
            val blob = Blob(arrayOf(showJson), jsObject { type = contentType })
            navigator.asDynamic().msSaveOrOpenBlob(blob, filename);
        } else {
            val a = document.createElement("a") as HTMLAnchorElement
            a.download = filename;
            a.href = "data:${contentType},${encodeURIComponent(showJson)}"
            a.target = "_blank"
            document.body!!.appendChild(a)
            a.click()
            document.body!!.removeChild(a)
        }
    }
}