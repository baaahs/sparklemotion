@file:JsModule("ace-builds")
@file:Suppress("INTERFACE_WITH_SUPERCLASS")

package acex

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.js.Json
import kotlin.js.RegExp

external fun <T: Any> require(name: String): T?

external interface AceExt
external interface AceMode
external interface AceTheme

external interface Anchor : EventEmitter {
    fun getPosition(): Position
    fun getDocument(): Document
    fun setPosition(row: Number, column: Number, noClip: Boolean = definedExternally)
    fun detach()
    fun attach(doc: Document)
}

external interface Document : EventEmitter {
    fun setValue(text: String)
    fun getValue(): String
    fun createAnchor(row: Number, column: Number): Anchor
    fun getNewLineCharacter(): String
    fun setNewLineMode(newLineMode: String /* "auto" | "unix" | "windows" */)
    fun getNewLineMode(): String /* "auto" | "unix" | "windows" */
    fun isNewLine(text: String): Boolean
    fun getLine(row: Number): String
    fun getLines(firstRow: Number, lastRow: Number): Array<String>
    fun getAllLines(): Array<String>
    fun getLength(): Number
    fun getTextRange(range: Range): String
    fun getLinesForRange(range: Range): Array<String>
    fun insert(position: Position, text: String): Position
    fun insertInLine(position: Position, text: String): Position
    fun insertNewLine(position: Point): Point
    fun clippedPos(row: Number, column: Number): Point
    fun clonePos(pos: Point): Point
    fun pos(row: Number, column: Number): Point
    fun insertFullLines(row: Number, lines: Array<String>)
    fun insertMergedLines(position: Position, lines: Array<String>): Point
    fun remove(range: Range): Position
    fun removeInLine(row: Number, startColumn: Number, endColumn: Number): Position
    fun removeFullLines(firstRow: Number, lastRow: Number): Array<String>
    fun removeNewLine(row: Number)
    fun replace(range: Range, text: String): Position
    fun applyDeltas(deltas: Array<Delta>)
    fun revertDeltas(deltas: Array<Delta>)
    fun applyDelta(delta: Delta, doNotValidate: Boolean = definedExternally)
    fun revertDelta(delta: Delta)
    fun indexToPosition(index: Number, startRow: Number): Position
    fun positionToIndex(pos: Position, startRow: Number = definedExternally): Number
}

external interface `T$1` {
    var fold: Fold
    var kind: String
}

external interface FoldLine {
    var folds: Array<Fold>
    var range: Range
    var start: Point
    var end: Point
    fun shiftRow(shift: Number)
    fun addFold(fold: Fold)
    fun containsRow(row: Number): Boolean
    fun walk(callback: Function<*>, endRow: Number = definedExternally, endColumn: Number = definedExternally)
    fun getNextFoldTo(row: Number, column: Number): `T$1`?
    fun addRemoveChars(row: Number, column: Number, len: Number)
    fun split(row: Number, column: Number): FoldLine
    fun merge(foldLineNext: FoldLine)
    fun idxToPosition(idx: Number): Point
}

external interface Fold {
    var range: Range
    var start: Point
    var end: Point
    var foldLine: FoldLine?
        get() = definedExternally
        set(value) = definedExternally
    var sameRow: Boolean
    var subFolds: Array<Fold>
    fun setFoldLine(foldLine: FoldLine)
    fun clone(): Fold
    fun addSubFold(fold: Fold): Fold
    fun restoreRange(range: Range)
}

external interface `T$2` {
    var range: Range?
        get() = definedExternally
        set(value) = definedExternally
    var firstRange: Range
}

external interface Folding {
    fun getFoldAt(row: Number, column: Number, side: Number): Fold
    fun getFoldsInRange(range: Range): Array<Fold>
    fun getFoldsInRangeList(ranges: Array<Range>): Array<Fold>
    fun getAllFolds(): Array<Fold>
    fun getFoldStringAt(row: Number, column: Number, trim: Number = definedExternally, foldLine: FoldLine = definedExternally): String?
    fun getFoldLine(docRow: Number, startFoldLine: FoldLine = definedExternally): FoldLine?
    fun getNextFoldLine(docRow: Number, startFoldLine: FoldLine = definedExternally): FoldLine?
    fun getFoldedRowCount(first: Number, last: Number): Number
    fun addFold(placeholder: String, range: Range = definedExternally): Fold
    fun addFold(placeholder: String): Fold
    fun addFold(placeholder: Fold, range: Range = definedExternally): Fold
    fun addFold(placeholder: Fold): Fold
    fun addFolds(folds: Array<Fold>)
    fun removeFold(fold: Fold)
    fun removeFolds(folds: Array<Fold>)
    fun expandFold(fold: Fold)
    fun expandFolds(folds: Array<Fold>)
    fun unfold(location: Number?, expandInner: Boolean = definedExternally): Array<Fold>?
    fun unfold(location: Number?): Array<Fold>?
    fun unfold(location: Point?, expandInner: Boolean = definedExternally): Array<Fold>?
    fun unfold(location: Point?): Array<Fold>?
    fun unfold(location: Range?, expandInner: Boolean = definedExternally): Array<Fold>?
    fun unfold(location: Range?): Array<Fold>?
    fun isRowFolded(docRow: Number, startFoldRow: FoldLine = definedExternally): Boolean
    fun getFoldRowEnd(docRow: Number, startFoldRow: FoldLine = definedExternally): Number
    fun getFoldRowStart(docRow: Number, startFoldRow: FoldLine = definedExternally): Number
    fun getFoldDisplayLine(foldLine: FoldLine, endRow: Number?, endColumn: Number?, startRow: Number?, startColumn: Number?): String
    fun getDisplayLine(row: Number, endColumn: Number?, startRow: Number?, startColumn: Number?): String
    fun toggleFold(tryToUnfold: Boolean = definedExternally)
    fun getCommentFoldRange(row: Number, column: Number, dir: Number): Range?
    fun foldAll(startRow: Number = definedExternally, endRow: Number = definedExternally, depth: Number = definedExternally)
    fun setFoldStyle(style: String)
    fun getParentFoldRangeData(row: Number, ignoreCurrent: Boolean = definedExternally): `T$2`
    fun toggleFoldWidget(toggleParent: Boolean = definedExternally)
    fun updateFoldWidgets(delta: Delta)
}

external class Range constructor(startRow: Number, startColumn: Number, endRow: Number, endColumn: Number) {
    var start: Point
    var end: Point
    fun isEqual(range: Range): Boolean
    override fun toString(): String
    fun contains(row: Number, column: Number): Boolean
    fun compareRange(range: Range): Number
    fun comparePoint(p: Point): Number
    fun containsRange(range: Range): Boolean
    fun intersects(range: Range): Boolean
    fun isEnd(row: Number, column: Number): Boolean
    fun isStart(row: Number, column: Number): Boolean
    fun setStart(row: Number, column: Number)
    fun setEnd(row: Number, column: Number)
    fun inside(row: Number, column: Number): Boolean
    fun insideStart(row: Number, column: Number): Boolean
    fun insideEnd(row: Number, column: Number): Boolean
    fun compare(row: Number, column: Number): Number
    fun compareStart(row: Number, column: Number): Number
    fun compareEnd(row: Number, column: Number): Number
    fun compareInside(row: Number, column: Number): Number
    fun clipRows(firstRow: Number, lastRow: Number): Range
    fun extend(row: Number, column: Number): Range
    fun isEmpty(): Boolean
    fun isMultiLine(): Boolean
    fun clone(): Range
    fun collapseRows(): Range
    fun toScreenRange(session: EditSession): Range
    fun moveBy(row: Number, column: Number)
}

external interface EditSessionOptions {
    var wrap: dynamic /* "off" | "free" | "printmargin" | Boolean | Number */
        get() = definedExternally
        set(value) = definedExternally
    var wrapMethod: String /* "code" | "text" | "auto" */
    var indentedSoftWrap: Boolean
    var firstLineNumber: Number
    var useWorker: Boolean
    var useSoftTabs: Boolean
    var tabSize: Number
    var navigateWithinSoftTabs: Boolean
    var foldStyle: String /* "markbegin" | "markbeginend" | "manual" */
    var overwrite: Boolean
    var newLineMode: String /* "auto" | "unix" | "windows" */
    var mode: String
}

external interface EditSessionOptionsPartial {
    var wrap: dynamic /* "off" | "free" | "printmargin" | Boolean? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var wrapMethod: String? /* "code" | "text" | "auto" */
        get() = definedExternally
        set(value) = definedExternally
    var indentedSoftWrap: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var firstLineNumber: Number?
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
    var navigateWithinSoftTabs: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var foldStyle: String? /* "markbegin" | "markbeginend" | "manual" */
        get() = definedExternally
        set(value) = definedExternally
    var overwrite: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var newLineMode: String? /* "auto" | "unix" | "windows" */
        get() = definedExternally
        set(value) = definedExternally
    var mode: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface VirtualRendererOptions {
    var animatedScroll: Boolean
    var showInvisibles: Boolean
    var showPrintMargin: Boolean
    var printMarginColumn: Number
    var printMargin: dynamic /* Boolean | Number */
        get() = definedExternally
        set(value) = definedExternally
    var showGutter: Boolean
    var fadeFoldWidgets: Boolean
    var showFoldWidgets: Boolean
    var showLineNumbers: Boolean
    var displayIndentGuides: Boolean
    var highlightIndentGuides: Boolean
    var highlightGutterLine: Boolean
    var hScrollBarAlwaysVisible: Boolean
    var vScrollBarAlwaysVisible: Boolean
    var fontSize: Number
    var fontFamily: String
    var maxLines: Number
    var minLines: Number
    var scrollPastEnd: Boolean
    var fixedWidthGutter: Boolean
    var customScrollbar: Boolean
    var theme: String
    var hasCssTransforms: Boolean
    var maxPixelHeight: Number
}

external interface VirtualRendererOptionsPartial {
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
    var printMargin: dynamic /* Boolean? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var showGutter: Boolean?
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
    var displayIndentGuides: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var highlightIndentGuides: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var highlightGutterLine: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var hScrollBarAlwaysVisible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var vScrollBarAlwaysVisible: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var fontSize: Number?
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
    var customScrollbar: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var theme: String?
        get() = definedExternally
        set(value) = definedExternally
    var hasCssTransforms: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var maxPixelHeight: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MouseHandlerOptions {
    var scrollSpeed: Number
    var dragDelay: Number
    var dragEnabled: Boolean
    var focusTimeout: Number
    var tooltipFollowsMouse: Boolean
}

external interface MouseHandlerOptionsPartial {
    var scrollSpeed: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dragDelay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var dragEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var focusTimeout: Number?
        get() = definedExternally
        set(value) = definedExternally
    var tooltipFollowsMouse: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface EditorOptions : EditSessionOptions, MouseHandlerOptions, VirtualRendererOptions {
    var selectionStyle: String
    var highlightActiveLine: Boolean
    var highlightSelectedWord: Boolean
    var readOnly: Boolean
    var copyWithEmptySelection: Boolean
    var cursorStyle: String /* "ace" | "slim" | "smooth" | "wide" */
    var mergeUndoDeltas: dynamic /* Boolean | "always" */
        get() = definedExternally
        set(value) = definedExternally
    var behavioursEnabled: Boolean
    var wrapBehavioursEnabled: Boolean
    var enableAutoIndent: Boolean
    var enableBasicAutocompletion: dynamic /* Boolean | Array<Completer> */
        get() = definedExternally
        set(value) = definedExternally
    var enableLiveAutocompletion: dynamic /* Boolean | Array<Completer> */
        get() = definedExternally
        set(value) = definedExternally
    var enableSnippets: Boolean
    var autoScrollEditorIntoView: Boolean
    var keyboardHandler: String?
    var placeholder: String
    var value: String
    var session: EditSession
    var relativeLineNumbers: Boolean
}

external interface EditorOptionsPartial : EditSessionOptionsPartial, MouseHandlerOptionsPartial, VirtualRendererOptionsPartial {
    var selectionStyle: String?
        get() = definedExternally
        set(value) = definedExternally
    var highlightActiveLine: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var highlightSelectedWord: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var readOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var copyWithEmptySelection: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var cursorStyle: String? /* "ace" | "slim" | "smooth" | "wide" */
        get() = definedExternally
        set(value) = definedExternally
    var mergeUndoDeltas: dynamic /* Boolean? | "always" */
        get() = definedExternally
        set(value) = definedExternally
    var behavioursEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wrapBehavioursEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var enableAutoIndent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var enableBasicAutocompletion: dynamic /* Boolean? | Array<Completer>? */
        get() = definedExternally
        set(value) = definedExternally
    var enableLiveAutocompletion: dynamic /* Boolean? | Array<Completer>? */
        get() = definedExternally
        set(value) = definedExternally
    var enableSnippets: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var autoScrollEditorIntoView: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var keyboardHandler: String?
        get() = definedExternally
        set(value) = definedExternally
    var placeholder: String?
        get() = definedExternally
        set(value) = definedExternally
    var value: String?
        get() = definedExternally
        set(value) = definedExternally
    var session: EditSession?
        get() = definedExternally
        set(value) = definedExternally
    var relativeLineNumbers: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SearchOptions {
    var needle: dynamic /* String | RegExp */
        get() = definedExternally
        set(value) = definedExternally
    var preventScroll: Boolean
    var backwards: Boolean
    var start: Range
    var skipCurrent: Boolean
    var range: Range
    var preserveCase: Boolean
    var regExp: Boolean
    var wholeWord: Boolean
    var caseSensitive: Boolean
    var wrap: Boolean
}

external interface SearchOptionsPartial {
    var needle: dynamic /* String? | RegExp? */
        get() = definedExternally
        set(value) = definedExternally
    var preventScroll: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var backwards: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var start: Range?
        get() = definedExternally
        set(value) = definedExternally
    var skipCurrent: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var range: Range?
        get() = definedExternally
        set(value) = definedExternally
    var preserveCase: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var regExp: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wholeWord: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var caseSensitive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wrap: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface EventEmitter {
    fun once(name: String, callback: Function<*>)
    fun setDefaultHandler(name: String, callback: Function<*>)
    fun removeDefaultHandler(name: String, callback: Function<*>)
    fun on(name: String, callback: Function<*>, capturing: Boolean = definedExternally)
    fun addEventListener(name: String, callback: Function<*>, capturing: Boolean = definedExternally)
    fun off(name: String, callback: Function<*>)
    fun removeListener(name: String, callback: Function<*>)
    fun removeEventListener(name: String, callback: Function<*>)
    fun removeAllListeners(name: String = definedExternally)
}

external interface Point {
    var row: Number
    var column: Number
}

external interface Delta {
    var action: String /* "insert" | "remove" */
    var start: Point
    var end: Point
    var lines: Array<String>
}

external interface Annotation {
    var row: Number?
        get() = definedExternally
        set(value) = definedExternally
    var column: Number?
        get() = definedExternally
        set(value) = definedExternally
    var text: String
    var type: String
}

external interface `T$3` {
    var mac: String?
        get() = definedExternally
        set(value) = definedExternally
    var win: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Command {
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var bindKey: dynamic /* String? | `T$3`? */
        get() = definedExternally
        set(value) = definedExternally
    var readOnly: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var exec: (editor: Editor, args: Any) -> Unit
}

external interface KeyboardHandler {
    var handleKeyboard: Function<*>
}

external interface MarkerLike {
    var range: Range?
        get() = definedExternally
        set(value) = definedExternally
    var type: String
    var renderer: MarkerRenderer?
        get() = definedExternally
        set(value) = definedExternally
    var clazz: String
    var inFront: Boolean
    var id: Number
    var update: ((html: Array<String>, marker: Any, session: EditSession, config: Any) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Token {
    var type: String
    var value: String
    var index: Number?
        get() = definedExternally
        set(value) = definedExternally
    var start: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Completion {
    var value: String
    var score: Number
    var meta: String?
        get() = definedExternally
        set(value) = definedExternally
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var caption: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Tokenizer {
    fun removeCapturingGroups(src: String): String
    fun createSplitterRegexp(src: String, flag: String = definedExternally): RegExp
    fun getLineTokens(line: String, startState: String): Array<Token>
    fun getLineTokens(line: String, startState: Array<String>): Array<Token>
}

external interface TokenIterator {
    fun getCurrentToken(): Token
    fun getCurrentTokenColumn(): Number
    fun getCurrentTokenRow(): Number
    fun getCurrentTokenPosition(): Point
    fun getCurrentTokenRange(): Range
    fun stepBackward(): Token
    fun stepForward(): Token
}

external interface `T$4`

external interface SyntaxMode {
    fun getTokenizer(): Tokenizer
    fun toggleCommentLines(state: Any, session: EditSession, startRow: Number, endRow: Number)
    fun toggleBlockComment(state: Any, session: EditSession, range: Range, cursor: Point)
    fun getNextLineIndent(state: Any, line: String, tab: String): String
    fun checkOutdent(state: Any, line: String, input: String): Boolean
    fun autoOutdent(state: Any, doc: Document, row: Number)
    fun createWorker(session: EditSession): Any
    fun createModeDelegates(mapping: `T$4`)
    fun transformAction(state: String, action: String, editor: Editor, session: EditSession, text: String): Any
    fun getKeywords(append: Boolean = definedExternally): Array<dynamic /* String | RegExp */>
    fun getCompletions(state: String, session: EditSession, pos: Point, prefix: String): Array<Completion>
}

external interface Config {
    fun get(key: String): Any
    fun set(key: String, value: Any)
    fun all(): Json
    fun moduleUrl(name: String, component: String = definedExternally): String
    fun setModuleUrl(name: String, subst: String): String
    fun loadModule(moduleName: String, onLoad: (module: Any) -> Unit = definedExternally)
    fun loadModule(moduleName: String)
    fun loadModule(moduleName: Any /* JsTuple<String, String> */, onLoad: (module: Any) -> Unit = definedExternally)
    fun loadModule(moduleName: Any /* JsTuple<String, String> */)
    fun init(packaged: Any): Any
    fun defineOptions(obj: Any, path: String, options: Json): Config
    fun resetOptions(obj: Any)
    fun setDefaultValue(path: String, name: String, value: Any)
    fun setDefaultValues(path: String, optionHash: Json)
}

external interface OptionsProvider {
    fun setOptions(optList: Json)
    fun getOptions(optionNames: Array<String> = definedExternally): Json
    fun getOptions(): Json
    fun getOptions(optionNames: Json = definedExternally): Json
    fun setOption(name: String, value: Any)
    fun getOption(name: String): Any
}

external interface `T$5` {
    var value: String
    var rev: Number
}

external interface UndoManager {
    fun addSession(session: EditSession)
    fun add(delta: Delta, allowMerge: Boolean, session: EditSession)
    fun addSelection(selection: String, rev: Number = definedExternally)
    fun startNewGroup()
    fun markIgnored(from: Number, to: Number = definedExternally)
    fun getSelection(rev: Number, after: Boolean = definedExternally): `T$5`
    fun getRevision(): Number
    fun getDeltas(from: Number, to: Number = definedExternally): Array<Delta>
    fun undo(session: EditSession, dontSelect: Boolean = definedExternally)
    fun redo(session: EditSession, dontSelect: Boolean = definedExternally)
    fun reset()
    fun canUndo(): Boolean
    fun canRedo(): Boolean
    fun bookmark(rev: Number = definedExternally)
    fun isAtBookmark(): Boolean
    fun hasUndo(): Boolean
    fun hasRedo(): Boolean
    fun isClean(): Boolean
    fun markClean(rev: Number = definedExternally)
}

external interface Position {
    var row: Number
    var column: Number
}

external interface `T$6` {
    var data: Fold
    var action: String
}

external interface `T$7` {
    var first: Number
    var last: Number
}

external interface `T$8` {
    var data: `T$7`
}

external interface `T$9`

external interface `T$10` {
    var min: Number
    var max: Number
}

external interface EditSession : EventEmitter, OptionsProvider {
    var selection: Selection
    fun on(name: String /* "changeFold" */, callback: (obj: `T$6`) -> Unit): Function<*>
    fun on(name: String /* "changeScrollLeft" | "changeScrollTop" */, callback: (scrollLeft: Number) -> Unit): Function<*>
    fun on(name: String /* "tokenizerUpdate" */, callback: (obj: `T$8`) -> Unit): Function<*>
    fun on(name: String /* "change" */, callback: () -> Unit): Function<*>
    var doc: Document
    fun setDocument(doc: Document)
    fun getDocument(): Document
    fun resetCaches()
    fun setValue(text: String)
    fun getValue(): String
    fun getSelection(): Selection
    fun getState(row: Number): String
    fun getTokens(row: Number): Array<Token>
    fun getTokenAt(row: Number, column: Number): Token?
    fun setUndoManager(undoManager: UndoManager)
    fun markUndoGroup()
    fun getUndoManager(): UndoManager
    fun getTabString(): String
    fun setUseSoftTabs(param_val: Boolean)
    fun getUseSoftTabs(): Boolean
    fun setTabSize(tabSize: Number)
    fun getTabSize(): Number
    fun isTabStop(position: Position): Boolean
    fun setNavigateWithinSoftTabs(navigateWithinSoftTabs: Boolean)
    fun getNavigateWithinSoftTabs(): Boolean
    fun setOverwrite(overwrite: Boolean)
    fun getOverwrite(): Boolean
    fun toggleOverwrite()
    fun addGutterDecoration(row: Number, className: String)
    fun removeGutterDecoration(row: Number, className: String)
    fun getBreakpoints(): Array<String>
    fun setBreakpoints(rows: Array<Number>)
    fun clearBreakpoints()
    fun setBreakpoint(row: Number, className: String)
    fun clearBreakpoint(row: Number)
    fun addMarker(range: Range, className: String, type: String /* "fullLine" | "screenLine" | "text" */, inFront: Boolean = definedExternally): Number
    fun addMarker(range: Range, className: String, type: String /* "fullLine" | "screenLine" | "text" */): Number
    fun addMarker(range: Range, className: String, type: MarkerRenderer, inFront: Boolean = definedExternally): Number
    fun addMarker(range: Range, className: String, type: MarkerRenderer): Number
    fun addDynamicMarker(marker: MarkerLike, inFront: Boolean): MarkerLike
    fun removeMarker(markerId: Number)
    fun getMarkers(inFront: Boolean = definedExternally): `T$9`
    fun highlight(re: RegExp)
    fun highlightLines(startRow: Number, endRow: Number, className: String, inFront: Boolean = definedExternally): Range
    fun setAnnotations(annotations: Array<Annotation>)
    fun getAnnotations(): Array<Annotation>
    fun clearAnnotations()
    fun getWordRange(row: Number, column: Number): Range
    fun getAWordRange(row: Number, column: Number): Range
    fun setNewLineMode(newLineMode: String /* "auto" | "unix" | "windows" */)
    fun getNewLineMode(): String /* "auto" | "unix" | "windows" */
    fun setUseWorker(useWorker: Boolean)
    fun getUseWorker(): Boolean
    fun setMode(mode: String, callback: () -> Unit = definedExternally)
    fun setMode(mode: String)
    fun setMode(mode: SyntaxMode, callback: () -> Unit = definedExternally)
    fun setMode(mode: SyntaxMode)
    fun getMode(): SyntaxMode
    fun setScrollTop(scrollTop: Number)
    fun getScrollTop(): Number
    fun setScrollLeft(scrollLeft: Number)
    fun getScrollLeft(): Number
    fun getScreenWidth(): Number
    fun getLineWidgetMaxWidth(): Number
    fun getLine(row: Number): String
    fun getLines(firstRow: Number, lastRow: Number): Array<String>
    fun getLength(): Number
    fun getTextRange(range: Range): String
    fun insert(position: Position, text: String)
    fun remove(range: Range)
    fun removeFullLines(firstRow: Number, lastRow: Number)
    fun undoChanges(deltas: Array<Delta>, dontSelect: Boolean = definedExternally)
    fun redoChanges(deltas: Array<Delta>, dontSelect: Boolean = definedExternally)
    fun setUndoSelect(enable: Boolean)
    fun replace(range: Range, text: String)
    fun moveText(fromRange: Range, toPosition: Position, copy: Boolean = definedExternally)
    fun indentRows(startRow: Number, endRow: Number, indentString: String)
    fun outdentRows(range: Range)
    fun moveLinesUp(firstRow: Number, lastRow: Number)
    fun moveLinesDown(firstRow: Number, lastRow: Number)
    fun duplicateLines(firstRow: Number, lastRow: Number)
    fun setUseWrapMode(useWrapMode: Boolean)
    fun getUseWrapMode(): Boolean
    fun setWrapLimitRange(min: Number, max: Number)
    fun adjustWrapLimit(desiredLimit: Number): Boolean
    fun getWrapLimit(): Number
    fun setWrapLimit(limit: Number)
    fun getWrapLimitRange(): `T$10`
    fun getRowLineCount(row: Number): Number
    fun getRowWrapIndent(screenRow: Number): Number
    fun getScreenLastRowColumn(screenRow: Number): Number
    fun getDocumentLastRowColumn(docRow: Number, docColumn: Number): Number
    fun getdocumentLastRowColumnPosition(docRow: Number, docColumn: Number): Position
    fun getRowSplitData(row: Number): String?
    fun getScreenTabSize(screenColumn: Number): Number
    fun screenToDocumentRow(screenRow: Number, screenColumn: Number): Number
    fun screenToDocumentColumn(screenRow: Number, screenColumn: Number): Number
    fun screenToDocumentPosition(screenRow: Number, screenColumn: Number, offsetX: Number = definedExternally): Position
    fun documentToScreenPosition(docRow: Number, docColumn: Number): Position
    fun documentToScreenPosition(position: Position): Position
    fun documentToScreenColumn(row: Number, docColumn: Number): Number
    fun documentToScreenRow(docRow: Number, docColumn: Number): Number
    fun getScreenLength(): Number
    fun destroy()
}

external interface KeyBinding {
    fun setDefaultHandler(handler: KeyboardHandler)
    fun setKeyboardHandler(handler: KeyboardHandler)
    fun addKeyboardHandler(handler: KeyboardHandler, pos: Number = definedExternally)
    fun removeKeyboardHandler(handler: KeyboardHandler): Boolean
    fun getKeyboardHandler(): KeyboardHandler
    fun getStatusText(): String
    fun onCommandKey(e: Any, hashId: Number, keyCode: Number): Boolean
    fun onTextInput(text: String): Boolean
}

external interface CommandMap {
    @nativeGetter
    operator fun get(name: String): Command?
    @nativeSetter
    operator fun set(name: String, value: Command)
}

external interface `T$11` {
    var editor: Editor
    var command: Command
    var args: Array<Any>
}

external interface `T$12`

external interface `T$13` {
    var key: String
    var hashId: Number
}

external interface `T$14` {
    var command: String
}

external interface CommandManager : EventEmitter {
    var byName: CommandMap
    var commands: CommandMap
    fun on(name: String /* "exec" | "afterExec" */, callback: execEventHandler): Function<*>
    override fun once(name: String, callback: Function<*>)
    override fun setDefaultHandler(name: String, callback: Function<*>)
    override fun removeDefaultHandler(name: String, callback: Function<*>)
    override fun on(name: String, callback: Function<*>, capturing: Boolean)
    fun on(name: String, callback: Function<*>)
    override fun addEventListener(name: String, callback: Function<*>, capturing: Boolean)
    override fun off(name: String, callback: Function<*>)
    override fun removeListener(name: String, callback: Function<*>)
    override fun removeEventListener(name: String, callback: Function<*>)
    fun exec(command: String, editor: Editor, args: Any): Boolean
    fun toggleRecording(editor: Editor)
    fun replay(editor: Editor)
    fun addCommand(command: Command)
    fun addCommands(command: Array<Command>)
    fun removeCommand(command: Command, keepCommand: Boolean = definedExternally)
    fun removeCommand(command: Command)
    fun removeCommand(command: String, keepCommand: Boolean = definedExternally)
    fun removeCommand(command: String)
    fun removeCommands(command: Array<Command>)
    fun bindKey(key: String, command: Command, position: Number = definedExternally)
    fun bindKey(key: String, command: Command)
    fun bindKey(key: String, command: (editor: Editor) -> Unit, position: Number = definedExternally)
    fun bindKey(key: String, command: (editor: Editor) -> Unit)
    fun bindKey(key: `T$3`, command: Command, position: Number = definedExternally)
    fun bindKey(key: `T$3`, command: Command)
    fun bindKey(key: `T$3`, command: (editor: Editor) -> Unit, position: Number = definedExternally)
    fun bindKey(key: `T$3`, command: (editor: Editor) -> Unit)
    fun bindKeys(keys: `T$12`)
    fun parseKeys(keyPart: String): `T$13`
    fun findKeyCommand(hashId: Number, keyString: String): String?
    fun handleKeyboard(data: Any, hashId: Number, keyString: String, keyCode: String): dynamic /* Unit | `T$14` */
    fun handleKeyboard(data: Any, hashId: Number, keyString: String, keyCode: Number): dynamic /* Unit | `T$14` */
    fun getStatusText(editor: Editor, data: Any): String
}

external interface `T$15` {
    var pageX: Number
    var pageY: Number
}

external interface `T$16` {
    var row: Number
    var column: Number
    var side: dynamic /* 1 | "-1" */
        get() = definedExternally
        set(value) = definedExternally
    var offsetX: Number
}

external interface VirtualRenderer : OptionsProvider, EventEmitter {
    var container: HTMLElement
    var scroller: HTMLElement
    var content: HTMLElement
    var characterWidth: Number
    var lineHeight: Number
    var scrollLeft: Number
    var scrollTop: Number
    var `$padding`: Number
    fun setSession(session: EditSession)
    fun updateLines(firstRow: Number, lastRow: Number, force: Boolean = definedExternally)
    fun updateText()
    fun updateFull(force: Boolean = definedExternally)
    fun updateFontSize()
    fun adjustWrapLimit(): Boolean
    fun setAnimatedScroll(shouldAnimate: Boolean)
    fun getAnimatedScroll(): Boolean
    fun setShowInvisibles(showInvisibles: Boolean)
    fun getShowInvisibles(): Boolean
    fun setDisplayIndentGuides(display: Boolean)
    fun getDisplayIndentGuides(): Boolean
    fun setShowPrintMargin(showPrintMargin: Boolean)
    fun getShowPrintMargin(): Boolean
    fun setPrintMarginColumn(showPrintMargin: Boolean)
    fun getPrintMarginColumn(): Boolean
    fun setShowGutter(show: Boolean)
    fun getShowGutter(): Boolean
    fun setFadeFoldWidgets(show: Boolean)
    fun getFadeFoldWidgets(): Boolean
    fun setHighlightGutterLine(shouldHighlight: Boolean)
    fun getHighlightGutterLine(): Boolean
    fun getContainerElement(): HTMLElement
    fun getMouseEventTarget(): HTMLElement
    fun getTextAreaContainer(): HTMLElement
    fun getFirstVisibleRow(): Number
    fun getFirstFullyVisibleRow(): Number
    fun getLastFullyVisibleRow(): Number
    fun getLastVisibleRow(): Number
    fun setPadding(padding: Number)
    fun setScrollMargin(top: Number, bottom: Number, left: Number, right: Number)
    fun setHScrollBarAlwaysVisible(alwaysVisible: Boolean)
    fun getHScrollBarAlwaysVisible(): Boolean
    fun setVScrollBarAlwaysVisible(alwaysVisible: Boolean)
    fun getVScrollBarAlwaysVisible(): Boolean
    fun freeze()
    fun unfreeze()
    fun updateFrontMarkers()
    fun updateBackMarkers()
    fun updateBreakpoints()
    fun setAnnotations(annotations: Array<Annotation>)
    fun updateCursor()
    fun hideCursor()
    fun showCursor()
    fun scrollSelectionIntoView(anchor: Position, lead: Position, offset: Number = definedExternally)
    fun scrollCursorIntoView(cursor: Position, offset: Number = definedExternally)
    fun getScrollTop(): Number
    fun getScrollLeft(): Number
    fun getScrollTopRow(): Number
    fun getScrollBottomRow(): Number
    fun scrollToRow(row: Number)
    fun alignCursor(cursor: Position, alignment: Number): Number
    fun alignCursor(cursor: Number, alignment: Number): Number
    fun scrollToLine(line: Number, center: Boolean, animate: Boolean, callback: () -> Unit)
    fun animateScrolling(fromValue: Number, callback: () -> Unit)
    fun scrollToY(scrollTop: Number)
    fun scrollToX(scrollLeft: Number)
    fun scrollTo(x: Number, y: Number)
    fun scrollBy(deltaX: Number, deltaY: Number)
    fun isScrollableBy(deltaX: Number, deltaY: Number): Boolean
    fun textToScreenCoordinates(row: Number, column: Number): `T$15`
    fun pixelToScreenCoordinates(x: Number, y: Number): `T$16`
    fun visualizeFocus()
    fun visualizeBlur()
    fun showComposition(position: Number)
    fun setCompositionText(text: String)
    fun hideComposition()
    fun setTheme(theme: String, callback: () -> Unit = definedExternally)
    fun getTheme(): String
    fun setStyle(style: String, include: Boolean = definedExternally)
    fun unsetStyle(style: String)
    fun setCursorStyle(style: String)
    fun setMouseCursor(cursorStyle: String)
    fun attachToShadowRoot()
    fun destroy()
}

external interface Selection : EventEmitter {
    fun moveCursorWordLeft()
    fun moveCursorWordRight()
    fun fromOrientedRange(range: Range)
    fun setSelectionRange(match: Any)
    fun getAllRanges(): Array<Range>
    fun addRange(range: Range)
    fun isEmpty(): Boolean
    fun isMultiLine(): Boolean
    fun setCursor(row: Number, column: Number)
    fun setAnchor(row: Number, column: Number)
    fun getAnchor(): Position
    fun getCursor(): Position
    fun isBackwards(): Boolean
    fun getRange(): Range
    fun clearSelection()
    fun selectAll()
    fun setRange(range: Range, reverse: Boolean = definedExternally)
    fun selectTo(row: Number, column: Number)
    fun selectToPosition(pos: Any)
    fun selectUp()
    fun selectDown()
    fun selectRight()
    fun selectLeft()
    fun selectLineStart()
    fun selectLineEnd()
    fun selectFileEnd()
    fun selectFileStart()
    fun selectWordRight()
    fun selectWordLeft()
    fun getWordRange()
    fun selectWord()
    fun selectAWord()
    fun selectLine()
    fun moveCursorUp()
    fun moveCursorDown()
    fun moveCursorLeft()
    fun moveCursorRight()
    fun moveCursorLineStart()
    fun moveCursorLineEnd()
    fun moveCursorFileEnd()
    fun moveCursorFileStart()
    fun moveCursorLongWordRight()
    fun moveCursorLongWordLeft()
    fun moveCursorBy(rows: Number, chars: Number)
    fun moveCursorToPosition(position: Any)
    fun moveCursorTo(row: Number, column: Number, keepDesiredColumn: Boolean = definedExternally)
    fun moveCursorToScreen(row: Number, column: Number, keepDesiredColumn: Boolean)
    fun toJSON(): dynamic /* SavedSelection | Array<SavedSelection> */
    fun fromJSON(selection: SavedSelection)
    fun fromJSON(selection: Array<SavedSelection>)

    var session: EditSession
    var doc: Document

//    companion object
}

external interface SavedSelection {
    var start: Point
    var end: Point
    var isBackwards: Boolean
}

external interface TextInput {
    fun resetSelection()
}

external interface `T$17` {
    var data: String
}

external interface `T$18` {
    var session: EditSession
    var oldSession: EditSession
}

external interface `T$19` {
    var text: String
}

external interface Editor : OptionsProvider, EventEmitter {
    var container: HTMLElement
    var renderer: VirtualRenderer
    var id: String
    var commands: CommandManager
    var keyBinding: KeyBinding
    var session: EditSession
    var selection: Selection
    var textInput: TextInput
    fun on(name: String /* "blur" | "focus" */, callback: (e: Event) -> Unit)
    fun on(name: String /* "input" */, callback: () -> Unit)
    fun on(name: String /* "change" */, callback: (delta: Delta) -> Unit)
    fun on(name: String /* "changeSelectionStyle" */, callback: (obj: `T$17`) -> Unit)
    fun on(name: String /* "changeSession" */, callback: (obj: `T$18`) -> Unit)
    fun on(name: String /* "copy" | "paste" */, callback: (obj: `T$19`) -> Unit)
    fun on(name: String /* "mousemove" | "mouseup" | "mousewheel" | "click" */, callback: (e: Any) -> Unit)
    fun onPaste(text: String, event: Any)
    fun setKeyboardHandler(keyboardHandler: String, callback: () -> Unit = definedExternally)
    fun setKeyboardHandler(keyboardHandler: String)
    fun setKeyboardHandler(keyboardHandler: KeyboardHandler?)
    fun getKeyboardHandler(): String
    fun setSession(session: EditSession)
    fun getSession(): EditSession
    fun setValue(param_val: String, cursorPos: Number = definedExternally): String
    fun getValue(): String
    fun getSelection(): Selection
    fun resize(force: Boolean = definedExternally)
    fun setTheme(theme: String, callback: () -> Unit = definedExternally)
    fun getTheme(): String
    fun setStyle(style: String)
    fun unsetStyle(style: String)
    fun getFontSize(): String
    fun setFontSize(size: Number)
    fun focus()
    fun isFocused(): Boolean
    fun blur()
    fun getSelectedText(): String
    fun getCopyText(): String
    fun execCommand(command: String, args: Any = definedExternally): Boolean
    fun execCommand(command: String): Boolean
    fun execCommand(command: Array<String>, args: Any = definedExternally): Boolean
    fun execCommand(command: Array<String>): Boolean
    fun insert(text: String, pasted: Boolean = definedExternally)
    fun setOverwrite(overwrite: Boolean)
    fun getOverwrite(): Boolean
    fun toggleOverwrite()
    fun setScrollSpeed(speed: Number)
    fun getScrollSpeed(): Number
    fun setDragDelay(dragDelay: Number)
    fun getDragDelay(): Number
    fun setSelectionStyle(param_val: String)
    fun getSelectionStyle(): String
    fun setHighlightActiveLine(shouldHighlight: Boolean)
    fun getHighlightActiveLine(): Boolean
    fun setHighlightGutterLine(shouldHighlight: Boolean)
    fun getHighlightGutterLine(): Boolean
    fun setHighlightSelectedWord(shouldHighlight: Boolean)
    fun getHighlightSelectedWord(): Boolean
    fun setAnimatedScroll(shouldAnimate: Boolean)
    fun getAnimatedScroll(): Boolean
    fun setShowInvisibles(showInvisibles: Boolean)
    fun getShowInvisibles(): Boolean
    fun setDisplayIndentGuides(display: Boolean)
    fun getDisplayIndentGuides(): Boolean
    fun setShowPrintMargin(showPrintMargin: Boolean)
    fun getShowPrintMargin(): Boolean
    fun setPrintMarginColumn(showPrintMargin: Number)
    fun getPrintMarginColumn(): Number
    fun setReadOnly(readOnly: Boolean)
    fun getReadOnly(): Boolean
    fun setBehavioursEnabled(enabled: Boolean)
    fun getBehavioursEnabled(): Boolean
    fun setWrapBehavioursEnabled(enabled: Boolean)
    fun getWrapBehavioursEnabled(): Boolean
    fun setShowFoldWidgets(show: Boolean)
    fun getShowFoldWidgets(): Boolean
    fun setFadeFoldWidgets(fade: Boolean)
    fun getFadeFoldWidgets(): Boolean
    fun remove(dir: String /* "left" | "right" */ = definedExternally)
    fun removeWordRight()
    fun removeWordLeft()
    fun removeLineToEnd()
    fun splitLine()
    fun transposeLetters()
    fun toLowerCase()
    fun toUpperCase()
    fun indent()
    fun blockIndent()
    fun blockOutdent()
    fun sortLines()
    fun toggleCommentLines()
    fun toggleBlockComment()
    fun modifyNumber(amount: Number)
    fun removeLines()
    fun duplicateSelection()
    fun moveLinesDown()
    fun moveLinesUp()
    fun moveText(range: Range, toPosition: Point, copy: Boolean = definedExternally): Range
    fun copyLinesUp()
    fun copyLinesDown()
    fun getFirstVisibleRow(): Number
    fun getLastVisibleRow(): Number
    fun isRowVisible(row: Number): Boolean
    fun isRowFullyVisible(row: Number): Boolean
    fun selectPageDown()
    fun selectPageUp()
    fun gotoPageDown()
    fun gotoPageUp()
    fun scrollPageDown()
    fun scrollPageUp()
    fun scrollToRow(row: Number)
    fun scrollToLine(line: Number, center: Boolean, animate: Boolean, callback: () -> Unit)
    fun centerSelection()
    fun getCursorPosition(): Point
    fun getCursorPositionScreen(): Point
    fun getSelectionRange(): Range
    fun selectAll()
    fun clearSelection()
    fun moveCursorTo(row: Number, column: Number)
    fun moveCursorToPosition(pos: Point)
    fun jumpToMatching(select: Boolean, expand: Boolean)
    fun gotoLine(lineNumber: Number, column: Number, animate: Boolean)
    fun navigateTo(row: Number, column: Number)
    fun navigateUp(times: Number = definedExternally)
    fun navigateDown(times: Number = definedExternally)
    fun navigateLeft(times: Number = definedExternally)
    fun navigateRight(times: Number = definedExternally)
    fun navigateLineStart()
    fun navigateLineEnd()
    fun navigateFileEnd()
    fun navigateFileStart()
    fun navigateWordRight()
    fun navigateWordLeft()
    fun replace(replacement: String, options: SearchOptionsPartial = definedExternally): Number
    fun replaceAll(replacement: String, options: SearchOptionsPartial = definedExternally): Number
    fun getLastSearchOptions(): SearchOptionsPartial
    fun find(needle: String, options: SearchOptionsPartial = definedExternally, animate: Boolean = definedExternally): Range?
    fun find(needle: String): Range?
    fun find(needle: String, options: SearchOptionsPartial = definedExternally): Range?
    fun find(needle: RegExp, options: SearchOptionsPartial = definedExternally, animate: Boolean = definedExternally): Range?
    fun find(needle: RegExp): Range?
    fun find(needle: RegExp, options: SearchOptionsPartial = definedExternally): Range?
    fun findNext(options: SearchOptionsPartial = definedExternally, animate: Boolean = definedExternally)
    fun findPrevious(options: SearchOptionsPartial = definedExternally, animate: Boolean = definedExternally)
    fun findAll(needle: String, options: SearchOptionsPartial = definedExternally, additive: Boolean = definedExternally): Number
    fun findAll(needle: String): Number
    fun findAll(needle: String, options: SearchOptionsPartial = definedExternally): Number
    fun findAll(needle: RegExp, options: SearchOptionsPartial = definedExternally, additive: Boolean = definedExternally): Number
    fun findAll(needle: RegExp): Number
    fun findAll(needle: RegExp, options: SearchOptionsPartial = definedExternally): Number
    fun undo()
    fun redo()
    fun destroy()
    fun setAutoScrollEditorIntoView(enable: Boolean)
    var completers: Array<Completer>
}

external interface Completer {
    var identifierRegexps: Array<RegExp>?
        get() = definedExternally
        set(value) = definedExternally
    fun getCompletions(editor: Editor, session: EditSession, position: Point, prefix: String, callback: CompleterCallback)
}