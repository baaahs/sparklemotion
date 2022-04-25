package baaahs.app.ui

import baaahs.app.ui.editor.Editor
import baaahs.client.document.EditMode
import baaahs.getBang
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutableLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

val ShowUi = xComponent<ShowUiProps>("ShowUi") { props ->
    val appContext = useContext(appContext)
    val show = props.show
    val editMode = appContext.showManager.editMode
    observe(editMode)

    // TODO: Pick layout based on device characteristics.
    val currentLayoutName = "default"
    val currentLayout = show.openLayouts.formats[currentLayoutName]
        ?: error("no such layout $currentLayoutName")
    val layoutEditor = memo(currentLayoutName) {
        object : Editor<MutableLayout> {
            override fun edit(mutableShow: MutableShow, block: MutableLayout.() -> Unit) {
                mutableShow.editLayouts {
                    block(formats.getBang(currentLayoutName, "layout"))
                }
            }
        }
    }

    showLayout {
        attrs.show = show
        attrs.layout = currentLayout
        attrs.layoutEditor = layoutEditor
        attrs.onShowStateChange = props.onShowStateChange
    }
}

external interface ShowUiProps : Props {
    var show: OpenShow
    var onShowStateChange: () -> Unit
}

fun RBuilder.showUi(handler: RHandler<ShowUiProps>) =
    child(ShowUi, handler = handler)
