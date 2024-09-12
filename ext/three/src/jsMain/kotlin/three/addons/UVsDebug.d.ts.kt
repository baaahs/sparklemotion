@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLCanvasElement
import three.BufferGeometry
import three.NormalOrGLBufferAttributes

external fun UVsDebug(geometry: BufferGeometry<NormalOrGLBufferAttributes>, size: Number = definedExternally): HTMLCanvasElement