@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Group : Object3D {
    override var type: String /* 'Group' */
    open var isGroup: Boolean
}