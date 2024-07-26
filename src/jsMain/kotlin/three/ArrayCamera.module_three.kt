@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class ArrayCamera(cameras: Array<PerspectiveCamera> = definedExternally) : PerspectiveCamera {
    open var cameras: Array<PerspectiveCamera>
    open var isArrayCamera: Boolean
}