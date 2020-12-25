package baaahs.app.ui

import baaahs.Severity
import baaahs.ShowEditorState
import baaahs.app.ui.controls.editIconWithBadge
import baaahs.ui.*
import baaahs.util.UndoStack
import kotlinx.css.opacity
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarPosition
import materialui.components.appbar.enums.AppBarStyle
import materialui.components.button.enums.ButtonColor
import materialui.components.dialog.dialog
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.iconbutton.enums.IconButtonEdge
import materialui.components.iconbutton.enums.IconButtonStyle
import materialui.components.iconbutton.iconButton
import materialui.components.link.enums.LinkStyle
import materialui.components.link.link
import materialui.components.switches.switch
import materialui.components.toolbar.toolbar
import materialui.components.typography.enums.TypographyStyle
import materialui.components.typography.typographyH6
import materialui.icon
import materialui.icons.Icons
import org.w3c.dom.events.Event
import react.*
import react.dom.b
import react.dom.div
import react.dom.h4
import react.dom.i
import styled.css
import styled.styledDiv

val AppToolbar = xComponent<AppToolbarProps>("AppToolbar") { props ->
    val appContext = useContext(appContext)
    val themeStyles = appContext.allStyles.appUi
    val webClient = appContext.webClient

    val handleShowEditButtonClick = useCallback {
        appContext.openEditor(ShowEditIntent())
    }

    val undoStack = props.undoStack
    val handleUndo = handler("handleUndo", undoStack) { _: Event ->
        undoStack.undo().also { (show, showState) ->
            webClient.onShowEdit(show, showState, pushToUndoStack = false)
        }
        Unit
    }

    val handleRedo = handler("handleRedo", undoStack) { _: Event ->
        undoStack.redo().also { (show, showState) ->
            webClient.onShowEdit(show, showState, pushToUndoStack = false)
        }
        Unit
    }

    val show = webClient.openShow
    val showProblemsSeverity = webClient.showProblems.map { it.severity }.max()

    var showProblemsDialogIsOpen by state { false }
    val toggleProblems = useCallback { showProblemsDialogIsOpen = !showProblemsDialogIsOpen }
    val closeProblems = useCallback { _: Event, _: String -> showProblemsDialogIsOpen = false }

    appBar(themeStyles.appToolbar on AppBarStyle.root) {
        attrs.position = AppBarPosition.relative

        toolbar {
            iconButton {
                attrs.color = ButtonColor.inherit
                attrs.edge = IconButtonEdge.start
                attrs.onClickFunction = props.onMenuButtonClick.withEvent()
                icon(Icons.Menu)
            }

            typographyH6(themeStyles.title on TypographyStyle.root) {
                show?.let {
                    b { +show.title }
                    if (webClient.showIsModified) i { +" (Unsaved)" }
                }

                if (show != null && props.editMode) {
                    div(+themeStyles.editButton) {
                        attrs.onClickFunction = handleShowEditButtonClick.withEvent()

                        editIconWithBadge(show, props.editMode)
                    }
                }
            }

            div(+themeStyles.logotype) { +"Sparkle Motion™" }

            div(+themeStyles.appToolbarActions) {
                styledDiv {
                    if (!props.editMode && !webClient.showIsModified) css { opacity = 0 }
                    css {
                        transition("opacity", duration = .5.s, timing = Timing.linear)
                    }

                    iconButton(Styles.buttons on IconButtonStyle.root) {
                        icon(Icons.Undo)
                        attrs["disabled"] = !undoStack.canUndo()
                        attrs.onClickFunction = handleUndo

                        typographyH6 { +"Undo" }
                    }

                    iconButton(Styles.buttons on IconButtonStyle.root) {
                        icon(Icons.Redo)
                        attrs["disabled"] = !undoStack.canRedo()
                        attrs.onClickFunction = handleRedo

                        typographyH6 { +"Redo" }
                    }

                    if (webClient.showFile == null) {
                        iconButton(Styles.buttons on IconButtonStyle.root) {
                            icon(Icons.FileCopy)
                            attrs.onClickFunction = props.onSaveShowAs.withEvent()
                            typographyH6 { +"Save As…" }
                        }
                    } else {
                        iconButton(Styles.buttons on IconButtonStyle.root) {
                            icon(Icons.Save)
                            attrs["disabled"] = !webClient.showIsModified
                            attrs.onClickFunction = props.onSaveShow.withEvent()
                            typographyH6 { +"Save" }
                        }
                    }

                    formControlLabel {
                        attrs.control {
                            switch {
                                attrs.checked = props.editMode
                                attrs.onChangeFunction = props.onEditModeChange.withEvent()
                            }
                        }
                        attrs.label { typographyH6 { +"Design Mode" } }
                    }
                }

                div(+themeStyles.appToolbarProblemsIcon) {
                    if (showProblemsSeverity != null) {
                        val iconClass = showProblemsSeverity.cssClass
                        link(iconClass on LinkStyle.root) {
                            attrs.onClickFunction = toggleProblems.withEvent()
                            icon(showProblemsSeverity.icon)
                        }
                    }
                }
                if (showProblemsDialogIsOpen) {
                    dialog {
                        attrs.open = true
                        attrs.onClose = closeProblems

                        dialogTitle { +"Show Problems" }
                        dialogContent(+themeStyles.showProblemsDialogContent) {
                            webClient.showProblems.sortedByDescending { it.severity }.forEach { problem ->
                                val iconClass = "${themeStyles.showProblem.name} ${problem.severity.cssClass}"
                                div(iconClass) { icon(problem.severity.icon) }
                                div {
                                    h4 { +problem.title }
                                    problem.message?.let { div { +it } }
                                }
                            }
                        }
                    }
                }

                help {
                    attrs.divClass = themeStyles.appToolbarHelpIcon.name
                    attrs.inject(HelpText.appToolbar)
                }
            }
        }
    }
}

private val Severity.cssClass get() = name.toLowerCase() + "Severity"

external interface AppToolbarProps : RProps {
    var editMode: Boolean
    var onEditModeChange: () -> Unit
    var onMenuButtonClick: () -> Unit
    var undoStack: UndoStack<ShowEditorState>
    var onSaveShow: () -> Unit
    var onSaveShowAs: () -> Unit
}

fun RBuilder.appToolbar(handler: RHandler<AppToolbarProps>) =
    child(AppToolbar, handler = handler)