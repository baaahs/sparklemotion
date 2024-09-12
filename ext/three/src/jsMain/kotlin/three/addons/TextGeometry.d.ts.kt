@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

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

external open class TextGeometry(text: String, parameters: TextGeometryParameters = definedExternally) : ExtrudeGeometry {
    override var override: Any
    override val type: String /* String | "TextGeometry" */
}