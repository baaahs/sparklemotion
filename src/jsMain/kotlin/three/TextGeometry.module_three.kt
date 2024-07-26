@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface TextGeometryParameters {
    var font: Font
    var size: Number?
        get() = definedExternally
        set(value) = definedExternally
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
    var curveSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var bevelThickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelOffset: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$32` {
    var font: Font
    var size: Number
    var height: Number
    var curveSegments: Number
    var bevelEnabled: Boolean
    var bevelThickness: Number
    var bevelSize: Number
    var bevelOffset: Number
    var bevelSegments: Number
}

open external class TextGeometry(text: String, parameters: TextGeometryParameters) : ExtrudeGeometry {
    override var type: String
    open var parameters: `T$32`
}