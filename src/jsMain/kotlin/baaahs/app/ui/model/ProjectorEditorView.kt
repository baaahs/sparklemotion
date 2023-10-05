package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.gl.Display
import baaahs.gl.Mode
import baaahs.scene.EditingEntity
import baaahs.scene.MutableProjectorData
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import kotlinx.js.jso
import mui.material.Box
import mui.material.Container
import react.*
import react.dom.header

private val ProjectorEditorView = xComponent<ProjectorEditorProps>("ProjectorEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val displays = appContext.webClient.displays
    observe(displays)

    val mutableEntity = props.editingEntity.mutableEntity

    var selectedDisplay by state<Display?> { null }
    val handleDisplayChange by handler(mutableEntity) { display: Display? ->
        mutableEntity.displayName = display?.name ?: ""
        selectedDisplay = display
        props.editingEntity.onChange()
    }

    var selectedMode by state<Mode?> { null }
    val handleModeChange by handler(mutableEntity) { mode: Mode? ->
        mutableEntity.width = mode?.width
        mutableEntity.height = mode?.height
        selectedMode = mode
        props.editingEntity.onChange()
    }

    header { +"Projector" }

    Container {
        attrs.classes = jso { this.root = -styles.transformEditSection }

        Box {
            betterSelect<Display?> {
                attrs.label = "Display"
                attrs.values = listOf(null) + displays.all
                attrs.renderValueOption = { display -> buildElement { +(display?.name ?: "Any") } }
                attrs.value = selectedDisplay
                attrs.onChange = handleDisplayChange
            }
        }

        Box {
            betterSelect<Mode?> {
                attrs.label = "Mode"
                attrs.values = listOf(null) + (selectedDisplay?.modes ?: emptyList())
                attrs.renderValueOption = { mode ->
                    buildElement {
                        +(
                                mode?.toString()
                                    ?: selectedDisplay?.let { "Default (${it.defaultMode})" }
                                    ?: "Default"
                                )
                    }
                }
                attrs.value = selectedMode
                attrs.onChange = handleModeChange
            }
        }
    }
}

external interface ProjectorEditorProps : Props {
    var editingEntity: EditingEntity<out MutableProjectorData>
}

fun RBuilder.projectorEditor(handler: RHandler<ProjectorEditorProps>) =
    child(ProjectorEditorView, handler = handler)