@file:JsModule("three/examples/jsm/utils/BufferGeometryUtils")
@file:JsNonModule
package three.examples.jsm.utils

import three.js.BufferGeometry
import three.js.NormalOrGLBufferAttributes

external fun mergeGeometries(
    geometries: Array<BufferGeometry<*>>,
    useGroups: Boolean = definedExternally
): BufferGeometry<NormalOrGLBufferAttributes>