@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class WebGLProperties {
    open fun get(obj: Any): Any
    open fun remove(obj: Any)
    open fun update(obj: Any, key: Any, value: Any): Any
    open fun dispose()
}