@file:JsModule("three")
@file:JsNonModule
package three

open external class ArrayCamera(cameras: Array<PerspectiveCamera> = definedExternally) : PerspectiveCamera {
    open val isArrayCamera: Boolean
    open var cameras: Array<PerspectiveCamera>
}