@file:JsModule("three/examples/jsm/utils/BufferGeometryUtils")
@file:JsNonModule
package three.examples.jsm.utils

import three.BufferGeometry
import three.NormalOrGLBufferAttributes

external fun mergeGeometries(
    geometries: Array<BufferGeometry<*>>,
    useGroups: Boolean = definedExternally
): BufferGeometry<NormalOrGLBufferAttributes>