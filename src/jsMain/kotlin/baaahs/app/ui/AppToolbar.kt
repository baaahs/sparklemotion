package baaahs.app.ui

import baaahs.app.ui.controls.problemBadge
import baaahs.app.ui.editor.SceneEditIntent
import baaahs.app.ui.editor.ShowEditIntent
import baaahs.sm.webapi.Severity
import baaahs.ui.*
import csstype.ClassName
import kotlinx.css.opacity
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import kotlinx.html.js.onClickFunction
import kotlinx.js.jso
import materialui.icon
import mui.material.*
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import styled.inlineStyles

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
    val handleSceneEditButtonClick = callback {
        appContext.openSceneEditor(SceneEditIntent())
    }

    val handleUndo by mouseEventHandler(documentManager) { documentManager.undo() }
    val handleRedo by mouseEventHandler(documentManager) { documentManager.redo() }
    val handleSync by mouseEventHandler(documentManager) { documentManager.sync() }

    val handleSave by mouseEventHandler(documentManager) {
        appContext.notifier.launchAndReportErrors {
            documentManager.onSave()
        }
    }

    val handleSaveAs by mouseEventHandler(documentManager) {
        appContext.notifier.launchAndReportErrors {
            documentManager.onSaveAs()
        }
    }

    val show = showManager.openShow
    val showProblemsSeverity = showManager.showProblems.map { it.severity }.maxOrNull()

    var showProblemsDialogIsOpen by state { false }
    val toggleProblems = callback { showProblemsDialogIsOpen = !showProblemsDialogIsOpen }
    val closeProblems = callback { _: Event, _: String -> showProblemsDialogIsOpen = false }
    val editMode = props.editMode == true || props.appMode == AppMode.Scene

    AppBar {
        attrs.classes = jso { this.root = -themeStyles.appToolbar }
        attrs.position = AppBarPosition.relative

        Toolbar {
            IconButton {
                attrs.color = IconButtonColor.inherit
                attrs.edge = IconButtonEdge.start
                attrs.onClick = props.onMenuButtonClick.withMouseEvent()
                icon(mui.icons.material.Menu)
            }

            typographyH6 {
                attrs.classes = jso { this.root = -themeStyles.title }
                div(+themeStyles.titleHeader) { +"Show:" }

                if (show != null) {
                    b {
                        +show.title
                        showManager.file?.let { attrs["title"] = it.toString() }
                    }

                    if (showManager.isUnsaved) i { +" (Unsaved)" }
                    problemBadge(show, themeStyles.problemBadge)

                    if (props.appMode == AppMode.Show && editMode) {
                        span(+themeStyles.editButton) {
                            icon(mui.icons.material.Edit)
                            attrs.onClickFunction = handleShowEditButtonClick.withEvent()
                        }
                    }
                } else {
                    i { +"None" }
                }
            }

            typographyH6 {
                attrs.classes = jso { this.root = -themeStyles.title }

                div(+themeStyles.titleHeader) { +"Scene:" }

                if (scene != null) {
                    b {
                        +scene.title
                        sceneManager.file?.let { attrs["title"] = it.toString() }
                    }
                    if (sceneManager.isUnsaved) i { +" (Unsaved)" }

                    if (props.appMode == AppMode.Scene && editMode) {
                        span(+themeStyles.editButton) {
                            icon(mui.icons.material.Edit)
                            attrs.onClickFunction = handleSceneEditButtonClick.withEvent()
                        }
                    }
                } else {
                    i { +"None" }
                }
            }

            div(+themeStyles.logotype) { +"Sparkle Motion™" }

            div(+themeStyles.appToolbarActions) {
                div {
                    inlineStyles {
                        if (!editMode && !documentManager.isUnsaved) {
                            opacity = 0
                        }
                        transition("opacity", duration = .5.s, timing = Timing.linear)
                    }

                    IconButton {
                        icon(mui.icons.material.Undo)
                        attrs.disabled = !documentManager.canUndo
                        attrs.onClick = handleUndo

                        typographyH6 { +"Undo" }
                    }

                    IconButton {
                        icon(mui.icons.material.Redo)
                        attrs.disabled = !documentManager.canRedo
                        attrs.onClick = handleRedo

                        typographyH6 { +"Redo" }
                    }

                    if (props.appMode == AppMode.Scene) {
                        IconButton {
                            icon(mui.icons.material.Sync)
                            attrs.disabled = documentManager.isSynched
                            attrs.onClick = handleSync

                            typographyH6 { +"Sync" }
                        }
                    }

                    if (!documentManager.isLoaded) {
                        IconButton {
                            icon(mui.icons.material.FileCopy)
                            attrs.onClick = handleSaveAs
                            typographyH6 { +"Save As…" }
                        }
                    } else {
                        IconButton {
                            icon(mui.icons.material.Save)
                            attrs.disabled = !documentManager.isUnsaved
                            attrs.onClick = handleSave
                            typographyH6 { +"Save" }
                        }
                    }

                    FormControlLabel {
                        attrs.control = buildElement {
                            Switch {
                                attrs.checked = props.editMode
                                attrs.onChange = props.onEditModeChange.withTChangeEvent()
                            }
                        }
                        attrs.label = buildElement { typographyH6 { +"Design Mode" } }
                    }
                }

                div(+themeStyles.appToolbarProblemsIcon) {
                    if (showProblemsSeverity != null) {
                        val iconClass = ClassName(showProblemsSeverity.cssClass)
                        Link {
                            attrs.classes = jso { this.root = iconClass }
                            attrs.onClick = toggleProblems.withMouseEvent()
                            icon(showProblemsSeverity.icon)
                        }
                    }
                }
                if (showProblemsDialogIsOpen) {
                    Dialog {
                        attrs.open = true
                        attrs.onClose = closeProblems

                        DialogTitle { +"Show Problems" }
                        DialogContent {
                            attrs.classes = jso { root = -themeStyles.showProblemsDialogContent }
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