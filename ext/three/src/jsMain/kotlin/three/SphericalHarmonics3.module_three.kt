@file:JsModule("three")
@file:JsNonModule
package three

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
//    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): SphericalHarmonics3 /* this */
//    open fun fromArray(array: Array<Number>): SphericalHarmonics3 /* this */
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): SphericalHarmonics3 /* this */
    open fun fromArray(array: Array<Number>): SphericalHarmonics3 /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(): Array<Number>
//    open fun toArray(array: Array<Number> = definedExternally): Array<Number>
//    open fun toArray(array: Array<Number>, offset: Number = definedExternally): Array<Number>
//    open fun toArray(array: Array<Number>): Array<Number>
    open fun getAt(normal: Vector3, target: Vector3): Vector3
    open fun getIrradianceAt(normal: Vector3, target: Vector3): Vector3

    companion object {
        fun getBasisAt(normal: Vector3, shBasis: Array<Number>)
    }
}