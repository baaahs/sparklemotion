@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.NormalOrGLBufferAttributes
import three.Vector3

open external class ConvexGeometry(points: Array<Vector3> = definedExternally) : BufferGeometry<NormalOrGLBufferAttributes>