@file:JsModule("material-ui-search-bar")

package external.searchbar

import react.ElementType
import react.Props
import react.ReactNode

@JsName("default")
external val SearchBar: ElementType<SearchBarProps>

external interface SearchBarProps : Props {
    var cancelOnEscape: Boolean? // 		Whether to clear search on escape
    var classes: List<String> // 		Override or extend the styles applied to the component.
    var className: String // 	'' 	Custom top-level class
    var closeIcon: ReactNode // 	<ClearIcon style={{ color: grey[500] }} /> 	Override the close icon.
    var disabled: Boolean? // 	false 	Disables text field.
    var onCancelSearch: () -> Unit // 		Fired when the search is cancelled.
    var onChange: (String) -> Unit // 		Fired when the text value changes.
    var onRequestSearch: () -> Unit // 		Fired when the search icon is clicked.
    var placeholder: String // 	'Search' 	Sets placeholder text for the embedded text field.
    var searchIcon: ReactNode // 	<SearchIcon style={{ color: grey[500] }} /> 	Override the search icon.
    var style: Any // 	null 	Override the inline-styles of the root element.
    var value: String // 	'' 	The value of the text field.
}