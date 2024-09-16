@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.ExtrudeGeometry
import three.ExtrudeGeometryOptions

external interface TextGeometryParameters : ExtrudeGeometryOptions {
    var font: Font
    var size: Number?
        get() = definedExternally
        set(value) = definedExternally
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
    override var depth: Number?
        get() = definedExternally
        set(value) = definedExternally
    override var curveSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
    override var bevelEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    override var bevelThickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    override var bevelSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    override var bevelOffset: Number?
        get() = definedExternally
        set(value) = definedExternally
    override var bevelSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class TextGeometry(text: String, parameters: TextGeometryParameters = definedExternally) : ExtrudeGeometry {
    override var override: Any
    override val type: String /* String | "TextGeometry" */
}