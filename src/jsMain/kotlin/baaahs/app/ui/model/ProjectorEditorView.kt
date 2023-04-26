package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.gl.Mode
import baaahs.gl.Monitor
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
    val monitors = appContext.webClient.monitors
    observe(monitors)

    val mutableEntity = props.editingEntity.mutableEntity

    var selectedMonitor by state<Monitor?> { null }
    val handleMonitorChange by handler(mutableEntity) { monitor: Monitor? ->
        mutableEntity.monitorName = monitor?.name ?: ""
        selectedMonitor = monitor
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
            betterSelect<Monitor?> {
                attrs.label = "Monitor"
                attrs.values = listOf(null) + monitors.all
                attrs.renderValueOption = { monitor -> buildElement { +(monitor?.name ?: "Any") } }
                attrs.value = selectedMonitor
                attrs.onChange = handleMonitorChange
            }
        }

        Box {
            betterSelect<Mode?> {
                attrs.label = "Mode"
                attrs.values = listOf(null) + (selectedMonitor?.modes ?: emptyList())
                attrs.renderValueOption = { mode ->
                    buildElement {
                        +(
                                mode?.toString()
                                    ?: selectedMonitor?.let { "Default (${it.defaultMode})" }
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