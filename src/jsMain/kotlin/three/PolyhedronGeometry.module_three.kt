@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

external interface `T$27` {
    var vertices: Array<Number>
    var indices: Array<Number>
    var radius: Number
    var detail: Number
}

open external class PolyhedronBufferGeometry(vertices: Array<Number>, indices: Array<Number>, radius: Number = definedExternally, detail: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$27`
}

open external class PolyhedronGeometry(vertices: Array<Number>, indices: Array<Number>, radius: Number = definedExternally, detail: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$27`
}