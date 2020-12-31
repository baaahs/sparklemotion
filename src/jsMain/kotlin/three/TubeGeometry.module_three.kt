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

external interface `T$29` {
    var path: Curve<Vector3>
    var tubularSegments: Number
    var radius: Number
    var radialSegments: Number
    var closed: Boolean
}

open external class TubeGeometry(path: Curve<Vector3>, tubularSegments: Number = definedExternally, radius: Number = definedExternally, radiusSegments: Number = definedExternally, closed: Boolean = definedExternally) : Geometry {
    open var parameters: `T$29`
    open var tangents: Array<Vector3>
    open var normals: Array<Vector3>
    open var binormals: Array<Vector3>
}