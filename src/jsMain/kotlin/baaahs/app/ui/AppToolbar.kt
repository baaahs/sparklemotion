package baaahs.app.ui

import baaahs.app.ui.controls.problemBadge
import baaahs.app.ui.editor.SceneEditIntent
import baaahs.app.ui.editor.ShowEditIntent
import baaahs.client.document.DocumentManager
import baaahs.sm.webapi.Severity
import baaahs.ui.*
import csstype.ClassName
import kotlinx.css.opacity
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import kotlinx.html.js.onClickFunction
import kotlinx.html.unsafe
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.*
import mui.material.*
import mui.material.Link
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.dom.html.ReactHTML
import react.useContext
import styled.inlineStyles

val AppToolbar = xComponent<AppToolbarProps>("AppToolbar") { props ->
    val appContext = useContext(appContext)
    val themeStyles = appContext.allStyles.appUi
    val showManager = appContext.showManager
    observe(showManager)

    val sceneManager = appContext.sceneManager
    observe(sceneManager)
    val scene = sceneManager.scene

    val documentManager = props.documentManager

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

    val editMode = observe(props.documentManager.editMode)
    val handleEditModeChange by handler {
        props.documentManager.editMode.toggle()
    }

    AppBar {
        attrs.classes = jso { this.root = -themeStyles.appToolbar }
        attrs.component = ReactHTML.div
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
                    showManager.file?.let {
                        div(+themeStyles.titleFooter) {
                            icon(Save)
                            span { attrs.unsafe { +"&nbsp;" } }
                            +it.toString()
                        }
                    }
                    b { +show.title }
                    if (showManager.isUnsaved) i { +" (Unsaved)" }
                    problemBadge(show, themeStyles.problemBadge)

                    if (props.appMode == AppMode.Show) {
                        span(+themeStyles.editButton) {
                            icon(Edit)
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
                    sceneManager.file?.let {
                        div(+themeStyles.titleFooter) {
                            icon(Save)
                            span { attrs.unsafe { +"&nbsp;" } }
                            +it.toString()
                        }
                    }
                    b { +scene.title }
                    if (sceneManager.isUnsaved) i { +" (Unsaved)" }

                    if (props.appMode == AppMode.Scene) {
                        span(+themeStyles.editButton) {
                            icon(Edit)
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
                        if (!editMode.isOn && !documentManager.isUnsaved) {
                            opacity = 0
                        }
                        transition("opacity", duration = .5.s, timing = Timing.linear)
                    }

                    Button {
                        attrs.startIcon = +Undo
                        attrs.disabled = !documentManager.canUndo
                        attrs.variant = ButtonVariant.contained
                        attrs.size = Size.small
                        attrs.onClick = handleUndo

                        +"Undo"
                    }

                    Button {
                        attrs.startIcon = +Redo
                        attrs.disabled = !documentManager.canRedo
                        attrs.variant = ButtonVariant.contained
                        attrs.size = Size.small
                        attrs.onClick = handleRedo

                        +"Redo"
                    }

                    if (props.appMode == AppMode.Scene) {
                        Button {
                            attrs.startIcon = +Sync
                            attrs.disabled = documentManager.isSynced
                            attrs.variant = ButtonVariant.contained
                            attrs.size = Size.small
                            attrs.onClick = handleSync

                            +"Sync"
                        }
                    }

                    if (!documentManager.isLoaded) {
                        Button {
                            attrs.startIcon = +FileCopy
                            attrs.variant = ButtonVariant.contained
                            attrs.size = Size.small
                            attrs.onClick = handleSaveAs
                            +"Save As…"
                        }
                    } else {
                        Button {
                            attrs.startIcon = +Save
                            attrs.disabled = !documentManager.isUnsaved
                            attrs.variant = ButtonVariant.contained
                            attrs.size = Size.small
                            attrs.onClick = handleSave
                            +"Save"
                        }
                    }

                    ToggleButton {
                        attrs.classes = jso {
                            this.root = -themeStyles.editModeButton
                            this.selected = -themeStyles.editModeButtonSelected
                        }
//                        attrs.variant = ButtonVariant.contained
                        attrs.color = ToggleButtonColor.error
                        attrs.selected = editMode.isOn
                        attrs.onClick = handleEditModeChange.withTMouseEvent()

                        if (editMode.isOn) {
                            LockOpen { attrs.fontSize = SvgIconSize.small }
                        } else {
                            Lock { attrs.fontSize = SvgIconSize.small }
                        }
                        +"Edit"
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
    var documentManager: DocumentManager<*, *>.Facade
    var onMenuButtonClick: () -> Unit
}

fun RBuilder.appToolbar(handler: RHandler<AppToolbarProps>) =
    child(AppToolbar, handler = handler)