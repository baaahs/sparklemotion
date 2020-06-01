@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package Ace

typealias AceEditor = `T$13`

typealias MarkerRenderer = (html: Array<String>, range: Range, left: Number, top: Number, config: Any) -> Unit

typealias execEventHandler = (obj: `T$13`) -> Unit

typealias CompleterCallback = (error: Any, completions: Array<Completion>) -> Unit