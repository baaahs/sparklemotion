@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package acex

typealias AceEditor = `T$11`

typealias MarkerRenderer = (html: Array<String>, range: Range, left: Number, top: Number, config: Any) -> Unit

typealias execEventHandler = (obj: `T$11`) -> Unit

typealias CompleterCallback = (error: Any, completions: Array<Completion>) -> Unit