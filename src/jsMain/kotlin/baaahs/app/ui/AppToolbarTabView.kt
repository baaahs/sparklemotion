package baaahs.app.ui

import baaahs.app.ui.controls.problemBadge
import baaahs.client.document.DocumentManager
import baaahs.client.document.OpenDocument
import baaahs.show.live.OpenPatchHolder
import baaahs.ui.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.unsafe
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.Article
import mui.icons.material.Edit
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.b
import react.dom.div
import react.dom.i
import react.dom.span
import react.useContext

private val AppToolbarTabView = xComponent<AppToolbarTabProps>("AppToolbarTab") { props ->
    val appContext = useContext(appContext)
    val themeStyles = appContext.allStyles.appUi

    typographyH6 {
        attrs.classes = jso { this.root = -themeStyles.title }
        div(+themeStyles.titleHeader) { +"${props.value.name}:" }

        val document = props.document
        if (document != null) {
            props.documentManager.file?.let {
                div(+themeStyles.titleFooter) {
                    icon(Article)
                    span { attrs.unsafe { +"&nbsp;" } }
                    +it.toString()
                }
            }
            b { +document.title }
            if (props.documentManager.isUnsaved) i(+themeStyles.unsaved) { +"* (unsaved)" }
            if (document is OpenPatchHolder) {
                problemBadge(document, themeStyles.problemBadge)
            }

            if (props.currentAppMode == props.value) {
                span(+themeStyles.editButton) {
                    icon(Edit)
                    attrs.onClickFunction = props.onEditButtonClick.withEvent()
                }
            }
        } else {
            i(+themeStyles.noFile) { +"None" }
        }
    }
}

external interface AppToolbarTabProps : Props {
    var currentAppMode: AppMode
    var value: AppMode
    var document: OpenDocument?
    var documentManager: DocumentManager<*, *>.Facade
    var onEditButtonClick: () -> Unit
}

fun RBuilder.appToolbarTab(handler: RHandler<AppToolbarTabProps>) =
    child(AppToolbarTabView, handler = handler)