@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.NormalOrGLBufferAttributes

open external class TeapotGeometry(size: Number = definedExternally, segments: Number = definedExternally, bottom: Boolean = definedExternally, lid: Boolean = definedExternally, body: Boolean = definedExternally, fitLid: Boolean = definedExternally, blinn: Boolean = definedExternally) : BufferGeometry<NormalOrGLBufferAttributes>