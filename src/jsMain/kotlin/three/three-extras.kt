@file:JsQualifier("THREE")

package three

import info.laht.threekt.core.BufferAttribute
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.math.Euler
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Quaternion
import info.laht.threekt.math.Vector3

open external class Float32BufferAttribute(
    array: dynamic,
    itemSize: Int,
    normalized: Boolean = definedExternally
) : BufferAttribute

open external class BufferGeometryUtils() {
    companion object {
        fun mergeBufferGeometries(
            geometries: Array<BufferGeometry>,
            useGroups: Boolean = definedExternally
        ): BufferGeometry
    }
}

open external class Matrix4 {

    /**
     * A column-major list of matrix values.
     */
    val elements: DoubleArray

    /**
     * Creates and initializes the Matrix4 to the 4x4 identity matrix.
     */
    constructor()

    fun set(n11: Number, n12: Number, n13: Number, n14: Number,
            n21: Number, n22: Number, n23: Number, n24: Number,
            n31: Number, n32: Number, n33: Number, n34: Number,
            n41: Number, n42: Number, n43: Number, n44: Number)

    /**
     * Resets this matrix to the identity matrix.
     */
    fun identity () : Matrix4

    /**
     * Creates a new Matrix4 with identical elements to this one.
     */
    fun clone () : Matrix4

    /**
     * Copies the elements of matrix m into this matrix.
     */
    fun copy ( m: Matrix4) : Matrix4

    /**
     * Copies the translation component of the supplied matrix m into this matrix's translation component.
     */
    fun copyPosition ( m: Matrix4): Matrix4

    fun extractBasis (xAxis: Vector3, yAxis: Vector3, zAxis: Vector3) : Matrix4

    /**
     * Set this to the basis matrix consisting of the three provided basis vectors
     */
    fun makeBasis (xAxis: Vector3, yAxis: Vector3, zAxis: Vector3) : Matrix4

    fun extractRotation (m: Matrix4) : Matrix4

    fun makeRotationFromEuler ( euler: Euler) : Matrix4

    fun makeRotationFromQuaternion ( q: Quaternion): Quaternion

    fun lookAt (eye : Vector3, target: Vector3, up: Vector3): Matrix4

    /**
     * Post-multiplies this matrix by m.
     */
    fun multiply ( m: Matrix4) : Matrix4

    /**
     * Pre-multiplies this matrix by m.
     */
    fun premultiply ( m: Matrix4) : Matrix4

    /**
     * Sets this matrix to a x b.
     */
    fun multiplyMatrices (a: Matrix4, b: Matrix4) : Matrix4

    /**
     * Multiplies every component of the matrix by a scalar value s.
     */
    fun multiplyScalar ( s: Double ): Matrix4

    fun applyToBufferAttribute (attribute : BufferGeometry)

    fun determinant () : Double

    fun transpose () : Matrix4

    fun setPosition ( v: Vector3) : Matrix4

    /**
     * Set this matrix to the inverse of the passed matrix m, using the method outlined here.
     * If throwOnDegenerate is not set and the matrix is not invertible, set this to the 4x4 identity matrix.
     *
     * @m the matrix to take the inverse of.
     * @param throwOnDegenerate (optional) If true, throw an error if the matrix is degenerate (not invertible).
     */
    fun getInverse (m : Matrix4, throwOnDegenerate: Boolean = definedExternally )

    /**
     * Multiplies the columns of this matrix by vector v.
     */
    fun scale ( v: Vector3): Matrix4

    /**
     * Gets the maximum scale value of the 3 axes.
     */
    fun getMaxScaleOnAxis () : Double

    fun makeTranslation ( x: Double, y: Double, z: Double ) : Matrix4

    fun makeRotationX ( theta: Double ): Matrix4

    fun makeRotationY ( theta: Double ) : Matrix4

    fun makeRotationZ ( theta: Double ) : Matrix4

    fun makeRotationAxis (axis: Vector3, angle:Double ) : Matrix4

    fun makeScale ( x: Double, y: Double, z: Double ) : Matrix4

    fun makeShear ( x:Double, y:Double, z:Double ) : Matrix4

    fun compose (position: Vector3, quaternion: Vector3, scale: Vector3) : Matrix4

    fun decompose (position: Vector3, quaternion: Vector3, scale: Vector3) : Matrix4

    fun makePerspective ( left: Double, right: Double, top: Double, bottom: Double, near: Double, far:Double ) : Matrix4

    fun makeOrthographic ( left: Double, right: Double, top: Double, bottom: Double, near: Double, far: Double ) : Matrix4

    fun equals ( matrix: Matrix4) : Boolean

    fun fromArray ( array: DoubleArray, offset: Int = definedExternally ) : Matrix4

    fun toArray ( array: DoubleArray, offset: Int = definedExternally ) : DoubleArray

}

