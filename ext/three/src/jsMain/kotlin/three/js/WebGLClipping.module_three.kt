@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$52` {
    var value: Any
    var needsUpdate: Boolean
}

open external class WebGLClipping(properties: WebGLProperties) {
    open var uniform: `T$52`
    open var numPlanes: Number
    open var numIntersection: Number
    open fun init(planes: Array<Any>, enableLocalClipping: Boolean, camera: Camera): Boolean
    open fun beginShadows()
    open fun endShadows()
    open fun setState(material: Material, camera: Camera, useCache: Boolean)
}