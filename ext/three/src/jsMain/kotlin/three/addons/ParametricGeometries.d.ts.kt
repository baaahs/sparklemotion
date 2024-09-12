@file:JsQualifier("three.addons.ParametricGeometries")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons.ParametricGeometries

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
import three.addons.Vector3
import three.addons.Curve
import three.addons.ParametricGeometry

external fun klein(v: Number, u: Number, target: Vector3): Vector3

external fun plane(width: Number, height: Number): (u: Number, v: Number, target: Vector3) -> Vector3

external fun mobius(u: Number, t: Number, target: Vector3): Vector3

external fun mobius3d(u: Number, t: Number, target: Vector3): Vector3

external open class TubeGeometry(path: Curve<Vector3>, segments: Number = definedExternally, radius: Number = definedExternally, segmentsRadius: Number = definedExternally, closed: Boolean = definedExternally) : ParametricGeometry

external open class TorusKnotGeometry(radius: Number = definedExternally, tube: Number = definedExternally, segmentsT: Number = definedExternally, segmentsR: Number = definedExternally, p: Number = definedExternally, q: Number = definedExternally) : TubeGeometry

external open class SphereGeometry(size: Number, u: Number, v: Number) : ParametricGeometry

external open class PlaneGeometry(width: Number, depth: Number, segmentsWidth: Number, segmentsDepth: Number) : ParametricGeometry