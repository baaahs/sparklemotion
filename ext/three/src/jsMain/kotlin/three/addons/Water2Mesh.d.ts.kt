@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.Material
import three.Mesh
import three.NormalOrGLBufferAttributes

external interface Water2MeshOptions : WaterMeshOptions {
    var normalMap0: Any
    var normalMap1: Any
    var flowMap: Any?
        get() = definedExternally
        set(value) = definedExternally
    var color: Any?
        get() = definedExternally
        set(value) = definedExternally
    var flowDirection: Any?
        get() = definedExternally
        set(value) = definedExternally
    var flowSpeed: Number?
        get() = definedExternally
        set(value) = definedExternally
    var reflectivity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var scale: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Water2Mesh(geometry: BufferGeometry<*>, options: WaterMeshOptions) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open val isWater: Boolean
}