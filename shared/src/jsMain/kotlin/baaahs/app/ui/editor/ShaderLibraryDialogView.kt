package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.app.ui.library.resultsSummary
import baaahs.app.ui.shaderCard
import baaahs.app.ui.toolchainContext
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.withCache
import react.dom.html.ReactHTML.img
import baaahs.libraries.ShaderLibrary
import baaahs.show.Shader
import baaahs.show.Tag
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShader
import baaahs.ui.*
import baaahs.ui.components.collapsibleSearchBox
import baaahs.util.CacheBuilder
import baaahs.util.JsPlatform
import baaahs.util.globalLaunch
import js.objects.jso
import kotlinx.coroutines.Job
import kotlinx.css.*
import materialui.icon
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.TypographyVariant
import mui.material.styles.useTheme
import mui.system.useMediaQuery
import react.*
import react.dom.div
import react.dom.events.MouseEvent
import react.dom.events.SyntheticEvent
import react.dom.html.ReactHTML.header
import styled.inlineStyles
import web.dom.Element
import mui.system.sx

class MenuHelper(private val forceRender: () -> Unit) {
    var anchor: Element? = null
        set(value) {
            field = value
            forceRender()
        }

    val isOpen get() = anchor != null

    val onToggleButtonClick: (MouseEvent<Element, *>, Any) -> Unit = { e, selected ->
        anchor = if (isOpen) null else e.target as Element
    }

    val onButtonClick: (MouseEvent<Element, *>) -> Unit = { e ->
        anchor = e.target as Element
    }

    val onMenuClose: () -> Unit = { close() }

    fun close() {
        anchor = null
        forceRender()
    }
}

fun XBuilder.menuHelper() =
    useMemo { MenuHelper() { forceRender() } }

enum class ItemSize(
    val icon: String,
    val previewSize: Int
) {
    SMALL("small", 1),
    MEDIUM("medium", 2),
    LARGE("large", 3)
}

private val ShaderLibraryDialogView = xComponent<ShaderLibraryDialogProps>("ShaderLibraryDialog") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.shaderLibrary
    val theme = useTheme<Theme>()
    val isSmallScreen = useMediaQuery(theme.isSmallScreen)
    val shaderLibraries = appContext.shaderLibraries
    val baseToolchain = useContext(toolchainContext)
    val toolchain = memo(baseToolchain) { baseToolchain.withCache("Shader Library") }

    var devWarningIsOpen by state { props.devWarning ?: false }
    val handleHideDevWarning by handler { _: SyntheticEvent<*, *>, _: SnackbarCloseReason -> devWarningIsOpen = false }
    val handleShowDevWarning by handler { _: Shader? -> devWarningIsOpen = true }
    val onSelect = props.onSelect ?: handleShowDevWarning

    val handleClose = callback(onSelect) { _: Any, _: String -> onSelect(null) }

    val previewSizeRange = 1..5
//    var previewSize by state { 3 }
//    val handleSmallerPreviewClick by mouseEventHandler { previewSize-- }
//    val handleBiggerPreviewClick by mouseEventHandler { previewSize++ }
    var itemSize by state { ItemSize.SMALL }
    val previewSize = itemSize.previewSize
    val handleSizeClick by handler { e: MouseEvent<Element, *>, value: Any ->
        itemSize = value as ItemSize
    }

    val previewSizePx = (previewSize * 75).px

    var adjustInputs by state { true }
    val handleAdjustInputsChange by switchEventHandler { _, checked ->
        adjustInputs = checked
    }

    val shaderStates = memo { ShaderStates() }
    val searchJob = ref<Job>()
    val entryDisplayCache = memo(onSelect) {
        CacheBuilder<ShaderLibrary.Entry, EntryDisplay> {
            EntryDisplay(it, onSelect) { entry, state ->
                shaderStates.onShaderStateChange(entry, state)
            }
        }
    }
    var knownTags by state { setOf<Tag>() }
    onMount {
        globalLaunch {
            knownTags = shaderLibraries.tagList()
        }
    }
    val tagSelections = memo { HashMap<Tag, Boolean>() }
    var matches by state { emptyList<EntryDisplay>() }
    val lastSearch = ref("")
    val runSearch by handler { terms: String ->
        searchJob.current?.cancel()
        searchJob.current = globalLaunch {
            lastSearch.current = terms
            val tagTerms = tagSelections.map { (term, b) ->
                if (b) term.fullString else term.minusString
            }.joinToString(" ")
            println("search tags: $tagTerms")
            var searchString = concat(terms, tagTerms)
            matches = shaderLibraries.searchFor(searchString)
//                .subList(0, 3)
                .map { entryDisplayCache[it] }
                .also {
                    shaderStates.onNewResults(it.size)
                }
        }
    }

    @Suppress("UNUSED_VARIABLE")
    val justOnce = memo { runSearch("") }

    val handleSearchChange by handler { value: String ->
        runSearch(value)
//        props.setValue(event.target.value)
//        props.editableManager.onChange(pushToUndoStack = false)
    }

    val handleTagSearchTermsChange by handler(runSearch) { map: Map<Tag, Boolean> ->
        tagSelections.clear()
        tagSelections.putAll(map)
        println(map)
        runSearch(lastSearch.current ?: "")
    }

    val filterMenu = menuHelper()
    val moreMenu = menuHelper()

    toolchainContext.Provider {
        attrs.value = toolchain

        Dialog {
            attrs.open = true
            attrs.fullWidth = true
            attrs.fullScreen = isSmallScreen
            attrs.maxWidth = "xl"
            attrs.TransitionComponent = Slide
            attrs.TransitionProps = jso<SlideProps> {
                this.direction = SlideDirection.up
            }
            attrs.scroll = DialogScroll.paper
            attrs.onClose = handleClose
            attrs.PaperProps = jso {
                this.classes = muiClasses { this.root = -styles.dialogPaper }
            }

            DialogTitle {
                attrs.className = -styles.dialogTitle
                attrs.component = header
                attrs.variant = TypographyVariant.body1

                div(+styles.dialogTitleDiv) {
                    +"Shaders"

                    collapsibleSearchBox {
                        attrs.className = -styles.searchBox
//                    attrs.searchString = controllerMatcher.searchString
                        attrs.onSearchChange = handleSearchChange
//                    attrs.onSearchRequest = handleSearchRequest
//                    attrs.onSearchCancel = handleSearchCancel
//                    attrs.onFocusChange = handleSearchBoxFocusChange
                    }

                    ToggleButton {
                        attrs.className = -styles.noPadding
                        attrs.value = true
                        attrs.selected = filterMenu.isOpen
                        attrs.onClick = filterMenu.onToggleButtonClick
                        icon(mui.icons.material.Tune)
                    }

                    IconButton {
                        attrs.onClick = moreMenu.onButtonClick
                        icon(CommonIcons.MoreVert)
                    }
                }
            }

            Menu {
                attrs.anchorEl = moreMenu.anchor.asDynamic()
                attrs.open = moreMenu.isOpen
                attrs.onClose = moreMenu.onMenuClose

                MenuItem {
//                    attrs.onClick = handleToggleAutoAdjustGadgets
                    ListItemText { +"Size:" }
                    ToggleButtonGroup {
                        attrs.exclusive = true
                        attrs.value = itemSize
                        attrs.onChange = handleSizeClick

                        ItemSize.entries.forEach { itemSize ->
                            ToggleButton {
                                img {
                                    attrs.className = -styles.resultsSizeIcons
                                    attrs.src = JsPlatform.imageUrl("/assets/items/$itemSize.svg")
                                }
                                attrs.value = itemSize
                            }
                        }
                    }
                }

                MenuItem {
                    FormControlLabel {
                        attrs.control = buildElement {
                            Switch {
                                attrs.checked = adjustInputs
                                attrs.onChange = handleAdjustInputsChange.withTChangeEvent()
                            }
                        }
                        attrs.label = "Adjust Inputs".asTextNode()
                    }
                }

                Divider {}

                MenuItem {
                    attrs.disabled = true
//                    attrs.onClick = handleToggleAutoAdjustGadgets
                    ListItemText { +"Manage Shader Libraries…" }
                }
            }

            DialogContent {
                attrs.className = -styles.dialogContent

                Collapse {
                    attrs.`in` = filterMenu.isOpen
                    Box {
                        attrs.sx {
                            paddingTop = theme.spacing(1)
                            paddingLeft = theme.spacing(3)
                            paddingRight = theme.spacing(3)
                            paddingBottom = theme.spacing(1)
                            backgroundColor = theme.palette.primary.dark.asColor()
                                .withAlpha(.40)
                                .blend(Color(theme.palette.background.paper))
                                .asColor()
                        }

                        +"Filter:"

                        tagSearchTerms {
                            attrs.tags = knownTags
                            attrs.onChange = handleTagSearchTermsChange
                        }
                    }
                }

                Box {
                    attrs.className = -styles.dialogHeader

                    if (isSmallScreen) {
                        Divider {}
                    }

                    div {
                        inlineStyles { flex = Flex.GROW }
                    }

                    resultsSummary {
                        attrs.shaderStates = shaderStates
                    }
                }

                Divider {}

                val matchesShaderCards = this@xComponent.memo(
                    matches, toolchain, previewSizePx, isSmallScreen, adjustInputs
                ) {
                    buildElement {
                        div(+styles.results) {
                            sharedGlContext {
                                div(+styles.shaderGridScrollContainer) {
                                    div(+styles.shaderGrid) {
                                        inlineStyles {
                                            gridTemplateColumns =
                                                GridTemplateColumns("repeat(auto-fit, minmax($previewSizePx, 1fr))")
                                        }
                                        matches.forEach { match ->
                                            shaderCard {
                                                key = match.entry.id
                                                attrs.mutablePatch = match.mutablePatch
                                                attrs.subtitle = match.entry.id.split(":").first()
                                                attrs.toolchain = toolchain
                                                attrs.cardSize = previewSizePx
                                                attrs.dense = isSmallScreen
                                                attrs.adjustGadgets = adjustInputs
                                                attrs.onSelect = match.onSelect
                                                attrs.onDelete = null
                                                attrs.onShaderStateChange = match.onShaderStateChange
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                child(matchesShaderCards)

                if (devWarningIsOpen) {
                    Snackbar {
                        attrs.open = devWarningIsOpen
                        attrs.message =
                            "NOTE: Shader Library for dev purposes only, nothing useful happens when you select one."
                                .asTextNode()
                        attrs.autoHideDuration = 5000
                        attrs.onClose = handleHideDevWarning
                    }
                }
            }
        }
    }
}

class EntryDisplay(
    val entry: ShaderLibrary.Entry,
    onSelect: (Shader?) -> Unit,
    onShaderStateChange: (ShaderLibrary.Entry, ShaderBuilder.State) -> Unit
) {
    val mutablePatch = MutablePatch(MutableShader(entry.shader))
    val onSelect = { onSelect(entry.shader) }
    val onShaderStateChange = { state: ShaderBuilder.State -> onShaderStateChange(entry, state) }
}

class ShaderStates(
    shaderCount: Int? = null
) : Observable() {
    var shaderCount = shaderCount
        private set

    private val states: MutableMap<String, ShaderBuilder.State> = mutableMapOf()

    fun onShaderStateChange(entry: ShaderLibrary.Entry, state: ShaderBuilder.State) {
        states[entry.id] = state
        notifyChanged()
    }

    fun onNewResults(shaderCount: Int) {
        this.shaderCount = shaderCount
        notifyChanged()
    }

    val stateCount: Map<ShaderBuilder.State, Int>
        get() = states.values.groupingBy { it }.eachCount()
}

external interface ShaderLibraryDialogProps : Props {
    var onSelect: ((Shader?) -> Unit)?
    var devWarning: Boolean?
}

fun RBuilder.shaderLibraryDialog(handler: RHandler<ShaderLibraryDialogProps>) =
    child(ShaderLibraryDialogView, handler = handler)