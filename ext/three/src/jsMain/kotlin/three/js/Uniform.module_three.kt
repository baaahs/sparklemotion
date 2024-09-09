@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Uniform {
    constructor(value: Any)
    constructor(type: String, value: Any)
    open var type: String
    open var value: Any
    open var dynamic: Boolean
    open var onUpdateCallback: Function<*>
    open fun onUpdate(callback: Function<*>): Uniform
}