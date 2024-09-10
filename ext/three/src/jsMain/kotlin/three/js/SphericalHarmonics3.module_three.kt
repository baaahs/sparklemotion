@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

import js.array.ArrayLike
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

open external class SphericalHarmonics3 {
    open var coefficients: Array<Vector3>
    open val isSphericalHarmonics3: Boolean
    open fun set(coefficients: Array<Vector3>): SphericalHarmonics3
    open fun zero(): SphericalHarmonics3
    open fun add(sh: SphericalHarmonics3): SphericalHarmonics3
    open fun addScaledSH(sh: SphericalHarmonics3, s: Number): SphericalHarmonics3
    open fun scale(s: Number): SphericalHarmonics3
    open fun lerp(sh: SphericalHarmonics3, alpha: Number): SphericalHarmonics3
    open fun equals(sh: SphericalHarmonics3): Boolean
    open fun copy(sh: SphericalHarmonics3): SphericalHarmonics3
    open fun clone(): SphericalHarmonics3 /* this */
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): SphericalHarmonics3 /* this */
    open fun fromArray(array: Array<Number>): SphericalHarmonics3 /* this */
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): SphericalHarmonics3 /* this */
    open fun fromArray(array: ArrayLike<Number>): SphericalHarmonics3 /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(): Array<Number>
    open fun toArray(array: Array<Number> = definedExternally): Array<Number>
    open fun toArray(array: ArrayLike<Number>, offset: Number = definedExternally): ArrayLike<Number>
    open fun toArray(array: ArrayLike<Number>): ArrayLike<Number>
    open fun getAt(normal: Vector3, target: Vector3): Vector3
    open fun getIrradianceAt(normal: Vector3, target: Vector3): Vector3

    companion object {
        fun getBasisAt(normal: Vector3, shBasis: Array<Number>)
    }
}