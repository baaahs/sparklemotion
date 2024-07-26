@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Bone : Object3D {
    open var isBone: Boolean
    override var type: String /* 'Bone' */
}