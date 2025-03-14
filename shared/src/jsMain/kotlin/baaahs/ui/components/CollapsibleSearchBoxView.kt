package baaahs.ui.components

import baaahs.app.ui.appContext
import baaahs.mapper.styleIf
import baaahs.ui.*
import js.objects.jso
import mui.icons.material.Search
import mui.icons.material.StopCircle
import mui.material.*
import mui.system.sx
import react.*
import react.dom.events.ChangeEvent
import web.cssom.Transition
import web.cssom.em
import web.html.HTMLElement
import web.html.HTMLInputElement

private val CollapsibleSearchBoxView = xComponent<CollapsibleSearchBoxProps>("CollapsibleSearchBox") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.collapsibleSearchBox

    var searchFieldFocused by state { props.startFocused == true }
    props.onFocusChange?.invoke(searchFieldFocused)

    val searchFieldRef = useRef<HTMLElement>()
    val handleSearchBoxClick by mouseEventHandler(props.onFocusChange) { e ->
        searchFieldFocused = true
        props.onFocusChange?.invoke(true)
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
    val handleFocus by focusEventHandler(props.onFocusChange) { _ ->
        searchFieldFocused = true
        props.onFocusChange?.invoke(true)
    }
    val handleBlur by focusEventHandler(props.onFocusChange) { _ ->
        searchFieldFocused = false
        props.onFocusChange?.invoke(false)
    }

    val isSearching = props.isSearching == true

    FormControl {
        attrs.className = -styles.searchBoxFormControl and
                styleIf(props.alignRight, styles.alignRight) and props.className
        attrs.onClick = handleSearchBoxClick

        TextField<StandardTextFieldProps> {
            ref = searchFieldRef
            attrs.sx {
                val isOpen = searchFieldFocused || props.defaultSearchString?.isNotBlank() == true
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
            attrs.defaultValue = props.defaultSearchString

            attrs.onChange = handleSearchChange
            attrs.onFocus = handleFocus
            attrs.onBlur = handleBlur
        }

        props.helpText?.let {
            FormHelperText { child(it) }
        }
    }
}

external interface CollapsibleSearchBoxProps : PropsWithClassName {
    var defaultSearchString: String?
    var isSearching: Boolean?
    var startFocused: Boolean?
    var alignRight: Boolean?
    var helpText: ReactElement<*>?
    var onSearchChange: ((String) -> Unit)?
    var onSearchRequest: ((String) -> Unit)?
    var onSearchCancel: (() -> Unit)?
    var onFocusChange: ((focused: Boolean) -> Unit)?
}

fun RBuilder.collapsibleSearchBox(handler: RHandler<CollapsibleSearchBoxProps>) =
    child(CollapsibleSearchBoxView, handler = handler)