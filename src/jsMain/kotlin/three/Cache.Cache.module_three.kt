@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external var enabled: Boolean

external var files: Any

external fun add(key: String, file: Any)

external fun get(key: String): Any

external fun remove(key: String)

external fun clear()