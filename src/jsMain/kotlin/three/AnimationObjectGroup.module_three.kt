@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$49` {
    var total: Number
    var inUse: Number
}

external interface `T$50` {
    var bindingsPerObject: Number
    var objects: `T$49`
}

open external class AnimationObjectGroup(vararg args: Any) {
    open var uuid: String
    open var stats: `T$50`
    open var isAnimationObjectGroup: Boolean
    open fun add(vararg args: Any)
    open fun remove(vararg args: Any)
    open fun uncache(vararg args: Any)
}