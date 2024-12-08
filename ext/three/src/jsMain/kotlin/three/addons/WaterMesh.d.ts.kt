package three.addons

import three.BufferGeometry
import three.Material
import three.Mesh
import three.NormalOrGLBufferAttributes

external interface WaterMeshOptions {
    var resolution: Number?
        get() = definedExternally
        set(value) = definedExternally
    var waterNormals: Any
    var alpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var size: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sunColor: Any?
        get() = definedExternally
        set(value) = definedExternally
    var sunDirection: Any?
        get() = definedExternally
        set(value) = definedExternally
    var waterColor: Any?
        get() = definedExternally
        set(value) = definedExternally
    var distortionScale: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class WaterMesh(geometry: BufferGeometry<*>, options: WaterMeshOptions) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open val isWater: Boolean
    open var resolution: Number
    open var waterNormals: Any
    open var alpha: Any
    open var size: Any
    open var sunColor: Any
    open var sunDirection: Any
    open var waterColor: Any
    open var distortionScale: Any
}