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

open external class Face3 {
    constructor(a: Number, b: Number, c: Number, normal: Vector3 = definedExternally, color: Color = definedExternally, materialIndex: Number = definedExternally)
    constructor(a: Number, b: Number, c: Number, normal: Vector3 = definedExternally, vertexColors: Array<Color> = definedExternally, materialIndex: Number = definedExternally)
    constructor(a: Number, b: Number, c: Number, vertexNormals: Array<Vector3> = definedExternally, color: Color = definedExternally, materialIndex: Number = definedExternally)
    constructor(a: Number, b: Number, c: Number, vertexNormals: Array<Vector3> = definedExternally, vertexColors: Array<Color> = definedExternally, materialIndex: Number = definedExternally)
    open var a: Number
    open var b: Number
    open var c: Number
    open var normal: Vector3
    open var vertexNormals: Array<Vector3>
    open var color: Color
    open var vertexColors: Array<Color>
    open var materialIndex: Number
    open fun clone(): Face3 /* this */
    open fun copy(source: Face3): Face3 /* this */
}