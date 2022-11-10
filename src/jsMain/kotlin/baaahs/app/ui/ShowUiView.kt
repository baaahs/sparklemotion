package baaahs.app.ui

import baaahs.app.ui.editor.Editor
import baaahs.getBang
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutableLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.Keypress
import baaahs.ui.KeypressResult
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val ShowUiView = xComponent<ShowUiProps>("ShowUi") { props ->
    val appContext = useContext(appContext)
    val show = props.show
    val editMode = appContext.showManager.editMode
    observe(editMode)

    val keyboard = appContext.keyboard
    onMount(keyboard, editMode, props.onLayoutEditorDialogToggle, props.onShaderLibraryDialogToggle) {
        keyboard.handle { keypress, _ ->
            var result: KeypressResult? = null
            when (keypress) {
                Keypress("KeyD") -> editMode.toggle()
                Keypress("KeyL") -> props.onLayoutEditorDialogToggle()
                Keypress("KeyL", metaKey = true) -> props.onShaderLibraryDialogToggle()
                else -> result = KeypressResult.NotHandled
            }
            result ?: KeypressResult.Handled
        }
    }

    // TODO: Pick layout based on device characteristics.
    val currentLayoutName = show.openLayouts.currentFormatId
        ?: error("No current layout.")
    val currentLayout = show.openLayouts.formats[currentLayoutName]
        ?: error("No such layout $currentLayoutName.")
    val layoutEditor = memo(currentLayoutName) {
        object : Editor<MutableLayout> {
            override val title: String = "Layout editor"

            override fun edit(mutableShow: MutableShow, block: MutableLayout.() -> Unit) {
                mutableShow.editLayouts {
                    block(formats.getBang(currentLayoutName, "layout"))
                }
            }

            override fun delete(mutableShow: MutableShow) {
                mutableShow.editLayouts {
                    formats.remove(currentLayoutName)
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
    var onLayoutEditorDialogToggle: () -> Unit
    var onShaderLibraryDialogToggle: () -> Unit
}

fun RBuilder.showUi(handler: RHandler<ShowUiProps>) =
    child(ShowUiView, handler = handler)
