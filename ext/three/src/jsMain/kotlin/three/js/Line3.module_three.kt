package three.js

open external class Line3(start: Vector3 = definedExternally, end: Vector3 = definedExternally) {
    open var start: Vector3
    open var end: Vector3
    open fun set(start: Vector3 = definedExternally, end: Vector3 = definedExternally): Line3
    open fun clone(): Line3 /* this */
    open fun copy(line: Line3): Line3 /* this */
    open fun getCenter(target: Vector3): Vector3
    open fun delta(target: Vector3): Vector3
    open fun distanceSq(): Number
    open fun distance(): Number
    open fun at(t: Number, target: Vector3): Vector3
    open fun closestPointToPointParameter(point: Vector3, clampToLine: Boolean = definedExternally): Number
    open fun closestPointToPoint(point: Vector3, clampToLine: Boolean, target: Vector3): Vector3
    open fun applyMatrix4(matrix: Matrix4): Line3
    open fun equals(line: Line3): Boolean
}