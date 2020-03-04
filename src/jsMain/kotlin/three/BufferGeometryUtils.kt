@file:JsQualifier("THREE")
package three

import info.laht.threekt.core.BufferGeometry

open external class BufferGeometryUtils() {
    companion object {
        fun mergeBufferGeometries(
            geometries: Array<BufferGeometry>,
            useGroups: Boolean = definedExternally
        ): BufferGeometry
    }
}

open external class OrbitControls(camera: Any, thing: Any) {
    var minPolarAngle: Double
    var maxPolarAngle: Double
    var target: Any
    fun update()
}