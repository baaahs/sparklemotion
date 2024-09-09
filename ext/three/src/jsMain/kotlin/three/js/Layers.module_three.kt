@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Layers {
    open var mask: Number
    open fun set(channel: Number)
    open fun enable(channel: Number)
    open fun enableAll()
    open fun toggle(channel: Number)
    open fun disable(channel: Number)
    open fun disableAll()
    open fun test(layers: Layers): Boolean
}