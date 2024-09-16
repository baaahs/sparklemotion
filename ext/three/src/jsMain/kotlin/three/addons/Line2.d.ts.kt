@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.Material

open external class Line2(geometry: LineGeometry = definedExternally, material: LineMaterial = definedExternally) : LineSegments2 {
//    override var geometry: LineGeometry
//    override var material: LineMaterial
    open val isLine2: Boolean
}