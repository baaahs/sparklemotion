package baaahs.app.ui

import baaahs.app.ui.dev.devModeToolbarMenu
import baaahs.app.ui.editor.SceneEditIntent
import baaahs.app.ui.editor.ShowEditIntent
import baaahs.app.ui.settings.displaySettings
import baaahs.app.ui.settings.fullScreenToggleButton
import baaahs.client.document.DocumentManager
import baaahs.sm.webapi.Severity
import baaahs.ui.*
import baaahs.util.JsPlatform
import js.objects.jso
import kotlinx.css.PointerEvents
import kotlinx.css.opacity
import kotlinx.css.pointerEvents
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import materialui.icon
import mui.icons.material.*
import mui.icons.material.Menu
import mui.material.*
import mui.material.Link
import mui.material.Tab
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.useMediaQuery
import mui.system.sx
import react.*
import react.dom.div
import react.dom.h4
import react.dom.html.ReactHTML
import styled.inlineStyles
import web.cssom.ClassName
import web.cssom.Display
import web.cssom.pct

private val AppToolbarView = xComponent<AppToolbarProps>("AppToolbar") { props ->
    val appContext = useContext(appContext)
    val theme = useTheme<Theme>()
    val isSmallScreen = useMediaQuery(theme.isSmallScreen)
    val themeStyles = appContext.allStyles.appUi

    val showManager = appContext.showManager
    observe(showManager)

    val sceneManager = appContext.sceneManager
    observe(sceneManager)

    val documentManager = props.documentManager
    val featureFlags = documentManager.featureFlags

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

    val handleAppModeTabClick by syntheticEventHandler<AppMode>(props.onAppModeChange) { _, value ->
        props.onAppModeChange(value)
    }
    val handleAppModeToggle by mouseEventHandler(props.onAppModeChange, props.appMode) {
        props.onAppModeChange(
            when (props.appMode) {
                AppMode.Show -> AppMode.Scene
                AppMode.Scene -> AppMode.Show
            }
        )
    }

    val show = showManager.openShow
    val showProblemsSeverity = showManager.showProblems.maxOfOrNull { it.severity }

    var showProblemsDialogIsOpen by state { false }
    val toggleProblems = callback { showProblemsDialogIsOpen = !showProblemsDialogIsOpen }
    val closeProblems = callback { _: Any, _: String -> showProblemsDialogIsOpen = false }

    val editMode = observe(props.documentManager.editMode)
    val handleEditModeChange by handler(editMode) { editMode.toggle() }

    AppBar {
        attrs.className = -themeStyles.appBar
        attrs.component = ReactHTML.div
        attrs.position = AppBarPosition.relative

        Toolbar {
            attrs.className = -themeStyles.toolbar

            Box {
                attrs.sx { display = Display.flex }

                IconButton {
                    attrs.color = IconButtonColor.inherit
                    attrs.edge = IconButtonEdge.start
                    attrs.onClick = props.onMenuButtonClick.withMouseEvent()
                    icon(Menu)
                }

                if (isSmallScreen) {
                    ToggleButton {
                        attrs.classes = muiClasses {
                            root = -themeStyles.appToolbarModeToggleButton
                            selected = -themeStyles.appToolbarModeToggleButtonSelected
                        }
                        attrs.selected = props.appMode == AppMode.Scene
                        attrs.onChange = handleAppModeToggle.withTMouseEvent()
                        icon(CommonIcons.Settings)
                    }
                }
            }

            if (!isSmallScreen) {
                // Otherwise the document info is shown in the drawer.

                Tabs {
                    attrs.className = -themeStyles.appToolbarTabs
                    attrs.classes = muiClasses { indicator = -themeStyles.appToolbarTabIndicator }
                    attrs.value = props.appMode
                    attrs.onChange = handleAppModeTabClick

                    val tabClasses = muiClasses<TabClasses> {
                        root = -themeStyles.appToolbarTab
                        selected = -themeStyles.appToolbarTabSelected
                    }
                    Tab {
                        attrs.classes = tabClasses
                        attrs.value = AppMode.Show
                        attrs.label = buildElement {
                            appToolbarTab {
                                attrs.currentAppMode = props.appMode
                                attrs.value = AppMode.Show
                                attrs.document = show
                                attrs.documentManager = showManager
                                attrs.onEditButtonClick = handleShowEditButtonClick
                            }
                        }
                    }
                    Tab {
                        attrs.classes = tabClasses
                        attrs.value = AppMode.Scene
                        attrs.label = buildElement {
                            appToolbarTab {
                                attrs.currentAppMode = props.appMode
                                attrs.value = AppMode.Scene
                                attrs.document = sceneManager.openScene
                                attrs.documentManager = sceneManager
                                attrs.onEditButtonClick = handleSceneEditButtonClick
                            }
                        }
                    }
                }
            }


            div(+themeStyles.logotype) { +"Sparkle Motion™" }

            div(+themeStyles.appToolbarActions) {
                if (editMode.isAvailable) {
                    div(+themeStyles.appToolbarEditModeActions) {
                        inlineStyles {
                            if (!editMode.isOn && !documentManager.isUnsaved) {
                                opacity = 0
                                pointerEvents = PointerEvents.none
                            }
                            transition(::opacity, duration = .5.s, timing = Timing.linear)
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

                        if (!featureFlags.autoSync && props.appMode == AppMode.Scene) {
                            Button {
                                attrs.startIcon = +Sync
                                attrs.disabled = documentManager.isSynced
                                attrs.variant = ButtonVariant.contained
                                attrs.size = Size.small
                                attrs.onClick = handleSync

                                +"Sync"
                            }
                        }

                        if (!featureFlags.autoSave) {
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
                        }
                    }
                }

                ToggleButton {
                    attrs.className = -themeStyles.editModeButton
                    attrs.classes = muiClasses {
                        selected = -themeStyles.editModeButtonSelected
                    }
//                        attrs.variant = ButtonVariant.contained
                    attrs.color = ToggleButtonColor.error
                    attrs.selected = editMode.isOn
                    attrs.onClick = handleEditModeChange.withTMouseEvent()
                    attrs.value = true

                    if (editMode.isOn) {
                        LockOpen { attrs.fontSize = SvgIconSize.small }
                    } else {
                        Lock { attrs.fontSize = SvgIconSize.small }
                    }
                    +"Edit"
                }

                ButtonGroup {
                    attrs.className = -themeStyles.appToolbarButtonGroup

                    if (props.appMode == AppMode.Show) {
                        if (showProblemsSeverity != null) {
                            Tooltip {
                                attrs.title = "Show Problems".asTextNode()

                                IconButton {
                                    attrs.className = -themeStyles.appToolbarProblemsIcon
                                    Link {
                                        attrs.className = ClassName(showProblemsSeverity.cssClass)
                                        attrs.onClick = toggleProblems.withMouseEvent()
                                        icon(showProblemsSeverity.icon)
                                    }

                                    mui.material.Badge {
                                        attrs.sx = jso { height = 50.pct }
                                        attrs.badgeContent =
                                            showManager.showProblems.size.toString().asTextNode()
                                    }
                                }
                            }
                        }

                        if (appContext.uiSettings.developerMode) {
                            devModeToolbarMenu {}
                        }

                        displaySettings {}

                        if (JsPlatform.isBrowser) {
                            fullScreenToggleButton {}
                        }
                    }

                    if (!isSmallScreen) {
                        help {
                            attrs.divClass = themeStyles.appToolbarHelpIcon.name
                            attrs.inject(HelpText.appToolbar)
                        }
                    }
                }
            }
        }

        if (showProblemsDialogIsOpen) {
            Dialog {
                attrs.open = true
                attrs.onClose = closeProblems

                DialogTitle { +"Show Problems" }
                DialogContent {
                    attrs.className = -themeStyles.showProblemsDialogContent
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
    }
}

private val Severity.cssClass get() = name.lowercase() + "Severity"

external interface AppToolbarProps : Props {
    var appMode: AppMode
    var documentManager: DocumentManager<*, *, *>.Facade
    var onMenuButtonClick: () -> Unit
    var onAppModeChange: (AppMode) -> Unit
}

fun RBuilder.appToolbar(handler: RHandler<AppToolbarProps>) =
    child(AppToolbarView, handler = handler)