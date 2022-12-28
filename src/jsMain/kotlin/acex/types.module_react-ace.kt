@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

package ReactAce.Ace

import dom.html.HTMLDivElement

external interface ICommandManager {
    var byName: Any
    var commands: Any
    var platform: String
    fun addCommands(commands: Array<Any>)
    fun addCommand(command: Any)
    fun exec(name: String, editor: Any, args: Any)
    val bindKey: ((bindKey: Any, command: Any) -> Unit)?
        get() = definedExternally
}

external interface IEditorProps {
    @nativeGetter
    operator fun get(index: String): Any?
    @nativeSetter
    operator fun set(index: String, value: Any)
    var `$blockScrolling`: dynamic /* Number | Boolean */
        get() = definedExternally
        set(value) = definedExternally
    var `$blockSelectEnabled`: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var `$enableBlockSelect`: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var `$enableMultiselect`: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var `$highlightPending`: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var `$highlightTagPending`: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var `$multiselectOnSessionChange`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onAddRange`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onChangeAnnotation`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onChangeBackMarker`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onChangeBreakpoint`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onChangeFold`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onChangeFrontMarker`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onChangeMode`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onChangeTabSize`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onChangeWrapLimit`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onChangeWrapMode`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onCursorChange`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onDocumentChange`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onMultiSelect`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onRemoveRange`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onScrollLeftChange`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onScrollTopChange`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onSelectionChange`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onSingleSelect`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
    var `$onTokenizerUpdate`: ((args: Any) -> Any)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IMarker {
    var startRow: Number
    var startCol: Number
    var endRow: Number
    var endCol: Number
    var className: String
    var type: dynamic /* "fullLine" | "screenLine" | "text" | Ace.MarkerRenderer */
        get() = definedExternally
        set(value) = definedExternally
    var inFront: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ICommandBindKey {
    var win: String
    var mac: String
}

external interface ICommand {
    var name: String
    var bindKey: ICommandBindKey
    fun exec(): Any
}

external interface IAceOptions {
    @nativeGetter
    operator fun get(index: String): Any?
    @nativeSetter
    operator fun set(index: String, value: Any)
    var selectionStyle: String /* "line" | "text" */
    var highlightActiveLine: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var highlightSelectedWord: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var readOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var cursorStyle: String /* "ace" | "slim" | "smooth" | "wide" */
    var mergeUndoDeltas: dynamic /* Boolean | "always" */
        get() = definedExternally
        set(value) = definedExternally
    var behavioursEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wrapBehavioursEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var autoScrollEditorIntoView: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var hScrollBarAlwaysVisible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var vScrollBarAlwaysVisible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var highlightGutterLine: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var animatedScroll: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var showInvisibles: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var showPrintMargin: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var printMarginColumn: Number?
        get() = definedExternally
        set(value) = definedExternally
    var printMargin: dynamic /* Boolean | Number */
        get() = definedExternally
        set(value) = definedExternally
    var fadeFoldWidgets: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var showFoldWidgets: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var showLineNumbers: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var showGutter: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var displayIndentGuides: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var fontSize: dynamic /* Number | String */
        get() = definedExternally
        set(value) = definedExternally
    var fontFamily: String?
        get() = definedExternally
        set(value) = definedExternally
    var maxLines: Number?
        get() = definedExternally
        set(value) = definedExternally
    var minLines: Number?
        get() = definedExternally
        set(value) = definedExternally
    var scrollPastEnd: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var fixedWidthGutter: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var theme: String?
        get() = definedExternally
        set(value) = definedExternally
    var scrollSpeed: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dragDelay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dragEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var focusTimout: Number?
        get() = definedExternally
        set(value) = definedExternally
    var tooltipFollowsMouse: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var firstLineNumber: Number?
        get() = definedExternally
        set(value) = definedExternally
    var overwrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var newLineMode: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var useWorker: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var useSoftTabs: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var tabSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var wrap: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var foldStyle: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var mode: String?
        get() = definedExternally
        set(value) = definedExternally
    var enableMultiselect: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var enableEmmet: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var enableBasicAutocompletion: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var enableLiveAutocompletion: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var enableSnippets: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var spellcheck: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var useElasticTabstops: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IAnnotation {
    var row: Number
    var column: Number
    var text: String
    var type: String
}

external interface IRenderer : acex.VirtualRenderer {
    var placeholderNode: HTMLDivElement?
        get() = definedExternally
        set(value) = definedExternally
}