@file:JsModule("three/examples/jsm/utils/BufferGeometryUtils")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three_ext

import three.js.BufferGeometry

open external class BufferGeometryUtils {
    companion object {
        fun mergeBufferGeometries(
            geometries: Array<BufferGeometry>,
            useGroups: Boolean = definedExternally
        ): BufferGeometry
    }
}