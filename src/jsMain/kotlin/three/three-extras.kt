@file:JsQualifier("THREE")

package three

import info.laht.threekt.core.BufferAttribute
import info.laht.threekt.core.BufferGeometry

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