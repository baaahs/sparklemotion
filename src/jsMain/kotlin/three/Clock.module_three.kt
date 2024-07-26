@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Clock(autoStart: Boolean = definedExternally) {
    open var autoStart: Boolean
    open var startTime: Number
    open var oldTime: Number
    open var elapsedTime: Number
    open var running: Boolean
    open fun start()
    open fun stop()
    open fun getElapsedTime(): Number
    open fun getDelta(): Number
}