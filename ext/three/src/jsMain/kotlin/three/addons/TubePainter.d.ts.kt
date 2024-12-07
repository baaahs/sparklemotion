package three.addons

import three.Matrix4
import three.Mesh
import three.Vector3

open external class TubePainter {
    open var mesh: Mesh<*, *>
    open fun stroke(position1: Vector3, position2: Vector3, matrix1: Matrix4, matrix2: Matrix4)
    open fun updateGeometry(start: Number, end: Number)
}