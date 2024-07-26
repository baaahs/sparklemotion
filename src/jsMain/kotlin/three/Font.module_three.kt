@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Font(jsondata: Any) {
    open var type: String
    open var data: String
    open fun generateShapes(text: String, size: Number): Array<Shape>
}