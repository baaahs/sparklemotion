@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.Mesh
import three.NormalOrGLBufferAttributes
import three.Vector3

external interface Curve {
    fun getPointAt(u: Number): Vector3
    fun getTangentAt(u: Number): Vector3
}

open external class RollerCoasterGeometry(curve: Curve, divisions: Number) : BufferGeometry<NormalOrGLBufferAttributes>

open external class RollerCoasterLiftersGeometry(curve: Curve, divisions: Number) : BufferGeometry<NormalOrGLBufferAttributes>

open external class RollerCoasterShadowGeometry(curve: Curve, divisions: Number) : BufferGeometry<NormalOrGLBufferAttributes>

open external class SkyGeometry : BufferGeometry<NormalOrGLBufferAttributes>

open external class TreesGeometry(landscape: Mesh<*, *>) : BufferGeometry<NormalOrGLBufferAttributes>