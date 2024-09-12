@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import js.array.ArrayLike
import three.Matrix3

open external class Volume(xLength: Number = definedExternally, yLength: Number = definedExternally, zLength: Number = definedExternally, type: String = definedExternally, arrayBuffer: ArrayLike<Number> = definedExternally) {
    open var xLength: Number
    open var yLength: Number
    open var zLength: Number
    open var axisOrder: Array<String /* "x" | "y" | "z" */>
    open var data: dynamic /* Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array */
    open var spacing: Array<Number>
    open var offset: Array<Number>
    open var matrix: Matrix3
    open var lowerThreshold: Number
    open var upperThreshold: Number
    open var sliceList: Array<VolumeSlice>
    open fun getData(i: Number, j: Number, k: Number): Number
    open fun access(i: Number, j: Number, k: Number): Number
    open fun reverseAccess(index: Number): Array<Number>
    open fun map(functionToMap: () -> Unit, context: Volume /* this */): Volume /* this */
    open fun extractPerpendicularPlane(axis: String, RASIndex: Number): Any?
    open fun extractSlice(axis: String, index: Number): VolumeSlice
    open fun repaintAllSlices(): Volume /* this */
    open fun computeMinMax(): Array<Number>
}