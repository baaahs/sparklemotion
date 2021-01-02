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

external interface `T$33` {
    var shape: Array<Vector2>
    var holes: Array<Array<Vector2>>
}

open external class Shape(points: Array<Vector2> = definedExternally) : Path {
    override var type: String
    open var holes: Array<Path>
    open fun extrude(options: Any = definedExternally): ExtrudeGeometry
    open fun makeGeometry(options: Any = definedExternally): ShapeGeometry
    open fun getPointsHoles(divisions: Number): Array<Array<Vector2>>
    open fun extractAllPoints(divisions: Number): `T$33`
    open fun extractPoints(divisions: Number): `T$33`
}