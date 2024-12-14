package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.scene.EditingEntity
import baaahs.ui.render
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import emotion.styled.styled
import materialui.icon
import mui.icons.material.Delete
import mui.icons.material.ExpandMore
import mui.material.*
import mui.system.sx
import react.*
import web.cssom.em
import web.cssom.pct
import web.cssom.px

private val EntityEditorView = xComponent<EntityEditorProps>("EntityEditor") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
    val styles = appContext.allStyles.modelEditor
    val editingEntity = props.editingEntity

    val handleDelete by mouseEventHandler(props.onDelete) {
        props.onDelete()
    }

    val MyAccordionDetails = memo {
        AccordionDetails.styled { x ->
            x.sx { padding = 0.px }
        }
    }

    FormControl {
        attrs.margin = FormControlMargin.dense
        attrs.sx { width = 100.pct }

        // Additional entity-specific views:
        editingEntity.getEditorPanels().forEachIndexed { i, editorPanel ->
            if (editorPanel.isMainPanelForEntityType) {
                Paper {
                    attrs.className = -styles.mainPanelForEntityType
                    attrs.elevation = 4
                    editingEntity.getView(editorPanel).render(this)
                }
            } else {
                Accordion {
                    attrs.elevation = 4
                    attrs.defaultExpanded = i == 0

                    AccordionSummary {
                        attrs.expandIcon = ExpandMore.create()
                        editorPanel.title?.let {
                            Typography { +it }
                        }
                    }
                    MyAccordionDetails {
                        editingEntity.getView(editorPanel).render(this)
                    }
                }
            }
        }

        Accordion {
            attrs.elevation = 4

            AccordionSummary {
                attrs.expandIcon = ExpandMore.create()
                Typography { +"Transformation" }
            }
            MyAccordionDetails {
                transformationEditor {
                    attrs.editingEntity = editingEntity
                }
            }
        }

        if (props.showTitleField == true) {
            titleAndDescriptionEditor {
                attrs.editingEntity = editingEntity
            }
        }

        // Actions
        if (props.hideActions != true) {
            Card {
                attrs.elevation = 1
                attrs.sx {
                    marginTop = 1.em
                    paddingTop = 1.em
                    paddingBottom = 1.em
                    border = "none".asDynamic()
                }

                IconButton {
                    attrs.size = Size.small
                    attrs.color = IconButtonColor.error
                    attrs.onClick = handleDelete
                    attrs.disabled = editMode.isOff
                    icon(Delete)
                    +"Delete ${editingEntity.mutableEntity.entityTypeTitle}"
                }
            }
        }
    }
}

external interface EntityEditorProps : Props {
    var showTitleField: Boolean?
    var editingEntity: EditingEntity<*>
    var hideActions : Boolean?
    var onDelete: () -> Unit
}

fun RBuilder.entityEditor(handler: RHandler<EntityEditorProps>) =
    child(EntityEditorView, handler = handler)