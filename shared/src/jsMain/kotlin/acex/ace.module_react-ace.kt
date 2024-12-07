@file:JsModule("react-ace")
@file:JsNonModule

package ReactAce.Ace

import react.*
import web.html.HTMLElement

external interface IAceEditorProps : Props {
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
//    var style: CSSProperties?
//        get() = definedExternally
//        set(value) = definedExternally
    var mode: dynamic /* String | Any? */
        get() = definedExternally
        set(value) = definedExternally
    var theme: String?
        get() = definedExternally
        set(value) = definedExternally
    var height: String?
        get() = definedExternally
        set(value) = definedExternally
    var width: String?
        get() = definedExternally
        set(value) = definedExternally
    var className: String?
        get() = definedExternally
        set(value) = definedExternally
    var fontSize: dynamic /* Number | String */
        get() = definedExternally
        set(value) = definedExternally
    var showGutter: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var showPrintMargin: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var highlightActiveLine: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var focus: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var cursorStart: Number?
        get() = definedExternally
        set(value) = definedExternally
    var wrapEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var readOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var minLines: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxLines: Number?
        get() = definedExternally
        set(value) = definedExternally
    var navigateToFileEnd: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var debounceChangePeriod: Number?
        get() = definedExternally
        set(value) = definedExternally
    var enableBasicAutocompletion: dynamic /* Boolean | Array<String> */
        get() = definedExternally
        set(value) = definedExternally
    var enableLiveAutocompletion: dynamic /* Boolean | Array<String> */
        get() = definedExternally
        set(value) = definedExternally
    var tabSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var value: String?
        get() = definedExternally
        set(value) = definedExternally
    var placeholder: String?
        get() = definedExternally
        set(value) = definedExternally
    var defaultValue: String?
        get() = definedExternally
        set(value) = definedExternally
    var scrollMargin: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var enableSnippets: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var onSelectionChange: ((value: Any, event: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCursorChange: ((value: Any, event: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onInput: ((event: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onLoad: ((editor: acex.Editor) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onValidate: ((annotations: Array<acex.Annotation>) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onBeforeLoad: ((ace: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onChange: ((value: String, event: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onSelection: ((selectedText: String, event: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCopy: ((value: String) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onPaste: ((value: String) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onFocus: ((event: Any, editor: acex.Editor) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onBlur: ((event: Any, editor: acex.Editor) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onScroll: ((editor: IEditorProps) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var editorProps: IEditorProps?
        get() = definedExternally
        set(value) = definedExternally
    var setOptions: IAceOptions?
        get() = definedExternally
        set(value) = definedExternally
    var keyboardHandler: String?
        get() = definedExternally
        set(value) = definedExternally
    var commands: Array<ICommand>?
        get() = definedExternally
        set(value) = definedExternally
    var annotations: Array<acex.Annotation>?
        get() = definedExternally
        set(value) = definedExternally
    var markers: Array<IMarker>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IAceEditorPropsPartial {
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
//    var style: CSSProperties?
//        get() = definedExternally
//        set(value) = definedExternally
    var mode: dynamic /* String | Any? */
        get() = definedExternally
        set(value) = definedExternally
    var theme: String?
        get() = definedExternally
        set(value) = definedExternally
    var height: String?
        get() = definedExternally
        set(value) = definedExternally
    var width: String?
        get() = definedExternally
        set(value) = definedExternally
    var className: String?
        get() = definedExternally
        set(value) = definedExternally
    var fontSize: dynamic /* Number | String */
        get() = definedExternally
        set(value) = definedExternally
    var showGutter: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var showPrintMargin: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var highlightActiveLine: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var focus: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var cursorStart: Number?
        get() = definedExternally
        set(value) = definedExternally
    var wrapEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var readOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var minLines: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxLines: Number?
        get() = definedExternally
        set(value) = definedExternally
    var navigateToFileEnd: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var debounceChangePeriod: Number?
        get() = definedExternally
        set(value) = definedExternally
    var enableBasicAutocompletion: dynamic /* Boolean | Array<String> */
        get() = definedExternally
        set(value) = definedExternally
    var enableLiveAutocompletion: dynamic /* Boolean | Array<String> */
        get() = definedExternally
        set(value) = definedExternally
    var tabSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var value: String?
        get() = definedExternally
        set(value) = definedExternally
    var placeholder: String?
        get() = definedExternally
        set(value) = definedExternally
    var defaultValue: String?
        get() = definedExternally
        set(value) = definedExternally
    var scrollMargin: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var enableSnippets: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var onSelectionChange: ((value: Any, event: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCursorChange: ((value: Any, event: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onInput: ((event: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onLoad: ((editor: acex.Editor) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onValidate: ((annotations: Array<acex.Annotation>) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onBeforeLoad: ((ace: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onChange: ((value: String, event: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onSelection: ((selectedText: String, event: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onCopy: ((value: String) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onPaste: ((value: String) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onFocus: ((event: Any, editor: acex.Editor) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onBlur: ((event: Any, editor: acex.Editor) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onScroll: ((editor: IEditorProps) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var editorProps: IEditorProps?
        get() = definedExternally
        set(value) = definedExternally
    var setOptions: IAceOptions?
        get() = definedExternally
        set(value) = definedExternally
    var keyboardHandler: String?
        get() = definedExternally
        set(value) = definedExternally
    var commands: Array<ICommand>?
        get() = definedExternally
        set(value) = definedExternally
    var annotations: Array<acex.Annotation>?
        get() = definedExternally
        set(value) = definedExternally
    var markers: Array<IMarker>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$0` {
    var text: String
}

@JsName("default")
external val reactAce : ElementType<IAceEditorProps>

//@JsName("default")
open external class ReactAce(props: IAceEditorProps) : Component<IAceEditorProps, State> {
    open var editor: Any
    open var refEditor: HTMLElement
    open var debounce: (fn: Any, delay: Number) -> (args: Any) -> Unit
    open var silent: Boolean
//    open fun componentDidMount()
    open fun componentDidUpdate(prevProps: IAceEditorProps)
    open fun handleScrollMargins(margins: Array<Number> = definedExternally)
//    open fun componentWillUnmount()
    open fun onChange(event: Any)
    open fun onSelectionChange(event: Any)
    open fun onCursorChange(event: Any)
    open fun onInput(event: Any = definedExternally)
    open fun onFocus(event: Any)
    open fun onBlur(event: Any)
    open fun onCopy(__0: `T$0`)
    open fun onPaste(__0: `T$0`)
    open fun onScroll()
    open fun handleOptions(props: IAceEditorProps)
    open fun handleMarkers(markers: Array<IMarker>)
    open fun updatePlaceholder()
    open fun updateRef(item: HTMLElement)
    override fun render(): ReactNode?

    companion object {
//        var propTypes: ValidationMap<IAceEditorProps>
        var defaultProps: IAceEditorPropsPartial
    }
}