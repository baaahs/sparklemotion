@file:JsQualifier("three.addons.ParametricGeometries")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons.ParametricGeometries

import three.Vector3
import three.addons.Curve
import three.addons.ParametricGeometry

external fun klein(v: Number, u: Number, target: Vector3): Vector3

external fun plane(width: Number, height: Number): (u: Number, v: Number, target: Vector3) -> Vector3

external fun mobius(u: Number, t: Number, target: Vector3): Vector3

external fun mobius3d(u: Number, t: Number, target: Vector3): Vector3

open external class TubeGeometry(path: Curve, segments: Number = definedExternally, radius: Number = definedExternally, segmentsRadius: Number = definedExternally, closed: Boolean = definedExternally) : ParametricGeometry

open external class TorusKnotGeometry(radius: Number = definedExternally, tube: Number = definedExternally, segmentsT: Number = definedExternally, segmentsR: Number = definedExternally, p: Number = definedExternally, q: Number = definedExternally) : TubeGeometry

open external class SphereGeometry(size: Number, u: Number, v: Number) : ParametricGeometry

open external class PlaneGeometry(width: Number, depth: Number, segmentsWidth: Number, segmentsDepth: Number) : ParametricGeometry