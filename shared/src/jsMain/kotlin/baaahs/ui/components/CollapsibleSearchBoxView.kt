package baaahs.ui.components

import baaahs.app.ui.appContext
import baaahs.ui.*
import js.objects.jso
import kotlinx.css.Color
import kotlinx.css.FlexDirection
import kotlinx.css.Position
import kotlinx.css.borderColor
import kotlinx.css.fieldset
import kotlinx.css.flexDirection
import kotlinx.css.position
import kotlinx.css.px
import kotlinx.css.right
import kotlinx.css.top
import kotlinx.css.width
import materialui.icon
import mui.icons.material.Search
import mui.icons.material.Stop
import mui.icons.material.StopCircle
import mui.material.FormControl
import mui.material.FormHelperText
import mui.material.Size
import mui.material.StandardTextFieldProps
import mui.material.TextField
import mui.material.styles.Theme
import mui.system.sx
import react.*
import react.dom.events.ChangeEvent
import styled.StyleSheet
import web.cssom.Transition
import web.cssom.em
import web.html.HTMLElement
import web.html.HTMLInputElement

private val CollapsibleSearchBoxView = xComponent<CollapsibleSearchBoxProps>("CollapsibleSearchBox") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.collapsibleSearchBox

    var searchFieldFocused by state { false }
    val searchFieldRef = useRef<HTMLElement>()
    val handleSearchBoxClick by mouseEventHandler { e ->
        searchFieldFocused = true
        (searchFieldRef.current?.querySelector("input") as? HTMLInputElement)
            ?.focus()
        e.preventDefault()
    }

    val handleSearchChange by changeEventHandler(props.onSearchChange) { event: ChangeEvent<*> ->
        props.onSearchChange?.invoke(event.target.value)
    }
    val handleSearchCancel by mouseEventHandler(props.onSearchCancel) { _ ->
        props.onSearchCancel?.invoke()
    }
    val handleFocus by focusEventHandler { _ -> searchFieldFocused = true }
    val handleBlur by focusEventHandler { _ -> searchFieldFocused = false }

    val isSearching = props.isSearching == true

    FormControl {
        attrs.className = -styles.searchBoxFormControl
        attrs.onClick = handleSearchBoxClick

        TextField<StandardTextFieldProps> {
            ref = searchFieldRef
            attrs.sx {
                val isOpen = searchFieldFocused || props.searchString?.isNotBlank() == true
                width = if (isOpen) 15.em else 3.em
                backgroundColor = if (isOpen) rgba(0, 0, 0, 0.25).asColor() else rgba(0, 0, 0, 0.0).asColor()
                transition = "width 300ms, background-color 300ms".unsafeCast<Transition>()
            }
            attrs.size = Size.small
            attrs.InputProps = jso {
                endAdornment = if (isSearching) {
                    StopCircle.create { this.onClick = handleSearchCancel }
                } else {
                    Search.create()
                }
            }
            attrs.defaultValue = props.searchString

            attrs.onChange = handleSearchChange
            attrs.onFocus = handleFocus
            attrs.onBlur = handleBlur
        }

        props.helpText?.let {
            FormHelperText { child(it) }
        }
    }
}

external interface CollapsibleSearchBoxProps : Props {
    var searchString: String?
    var isSearching: Boolean?
    var onSearchChange: ((String) -> Unit)?
    var onSearchRequest: ((String) -> Unit)?
    var onSearchCancel: (() -> Unit)?
    var helpText: ReactElement<*>?
}

fun RBuilder.collapsibleSearchBox(handler: RHandler<CollapsibleSearchBoxProps>) =
    child(CollapsibleSearchBoxView, handler = handler)