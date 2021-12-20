package baaahs.app.ui

import baaahs.app.ui.controls.problemBadge
import baaahs.sm.webapi.Severity
import baaahs.ui.*
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
import materialui.components.iconbutton.iconButton
import materialui.components.link.enums.LinkStyle
import materialui.components.link.link
import materialui.components.switches.switch
import materialui.components.toolbar.toolbar
import materialui.components.typography.enums.TypographyStyle
import materialui.components.typography.typographyH6
import materialui.icon
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.b
import react.dom.div
import react.dom.h4
import react.dom.i
import react.useContext
import styled.css
import styled.styledDiv

val AppToolbar = xComponent<AppToolbarProps>("AppToolbar") { props ->
    val appContext = useContext(appContext)
    val themeStyles = appContext.allStyles.appUi
    val showManager = appContext.showManager
    observe(showManager)

    val sceneManager = appContext.sceneManager
    observe(sceneManager)
    val scene = sceneManager.scene

    val documentManager = props.appMode.getDocumentManager(appContext)

    val handleShowEditButtonClick = callback {
        appContext.openEditor(ShowEditIntent())
    }

    val handleUndo by eventHandler(documentManager) { documentManager.undo() }
    val handleRedo by eventHandler(documentManager) { documentManager.redo() }

    val handleSave by eventHandler {
        appContext.notifier.launchAndReportErrors {
            documentManager.onSave()
        }
    }

    val handleSaveAs by eventHandler {
        appContext.notifier.launchAndReportErrors {
            documentManager.onSaveAs()
        }
    }

    val show = showManager.openShow
    val showProblemsSeverity = showManager.showProblems.map { it.severity }.maxOrNull()

    var showProblemsDialogIsOpen by state { false }
    val toggleProblems = callback { showProblemsDialogIsOpen = !showProblemsDialogIsOpen }
    val closeProblems = callback { _: Event, _: String -> showProblemsDialogIsOpen = false }
    val editMode = props.editMode == true

    appBar(themeStyles.appToolbar on AppBarStyle.root) {
        attrs.position = AppBarPosition.relative

        toolbar {
            iconButton {
                attrs.color = ButtonColor.inherit
                attrs.edge = IconButtonEdge.start
                attrs.onClickFunction = props.onMenuButtonClick.withEvent()
                icon(materialui.icons.Menu)
            }

            typographyH6(themeStyles.title on TypographyStyle.root) {
                show?.let {
                    b {
                        +show.title
                        showManager.file?.let { attrs["title"] = it.toString() }
                    }
                    if (showManager.isUnsaved) i { +" (Unsaved)" }

                    problemBadge(show, themeStyles.problemBadge)

                    if (props.appMode == AppMode.Show && editMode) {
                        div(+themeStyles.editButton) {
                            icon(materialui.icons.Edit)
                            attrs.onClickFunction = handleShowEditButtonClick.withEvent()
                        }
                    }
                }

                if (show != null && scene != null) +" | "

                scene?.let {
                    b {
                        +scene.title
                        sceneManager.file?.let { attrs["title"] = it.toString() }
                    }
                    if (sceneManager.isUnsaved) i { +" (Unsaved)" }

                    if (props.appMode == AppMode.Scene && editMode) {
                        div(+themeStyles.editButton) {
                            icon(materialui.icons.Edit)
                            attrs.onClickFunction = handleShowEditButtonClick.withEvent()
                        }
                    }
                }
            }

            div(+themeStyles.logotype) { +"Sparkle Motion™" }

            div(+themeStyles.appToolbarActions) {
                styledDiv {
                    if (!editMode && !showManager.isUnsaved) css { opacity = 0 }
                    css {
                        transition("opacity", duration = .5.s, timing = Timing.linear)
                    }

                    iconButton {
                        icon(materialui.icons.Undo)
                        attrs.disabled = !documentManager.canUndo
                        attrs.onClickFunction = handleUndo

                        typographyH6 { +"Undo" }
                    }

                    iconButton {
                        icon(materialui.icons.Redo)
                        attrs.disabled = !documentManager.canRedo
                        attrs.onClickFunction = handleRedo

                        typographyH6 { +"Redo" }
                    }

                    if (!showManager.isLoaded) {
                        iconButton {
                            icon(materialui.icons.FileCopy)
                            attrs.onClickFunction = handleSaveAs
                            typographyH6 { +"Save As…" }
                        }
                    } else {
                        iconButton {
                            icon(materialui.icons.Save)
                            attrs.disabled = !showManager.isUnsaved
                            attrs.onClickFunction = handleSave
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
                            showManager.showProblems.sortedByDescending { it.severity }.forEach { problem ->
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

private val Severity.cssClass get() = name.lowercase() + "Severity"

external interface AppToolbarProps : Props {
    var appMode: AppMode
    var editMode: Boolean?
    var onEditModeChange: () -> Unit
    var onMenuButtonClick: () -> Unit
}

fun RBuilder.appToolbar(handler: RHandler<AppToolbarProps>) =
    child(AppToolbar, handler = handler)