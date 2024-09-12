@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

external interface WaterOptions {
    var textureWidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var textureHeight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clipBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var alpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var time: Number?
        get() = definedExternally
        set(value) = definedExternally
    var waterNormals: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var sunDirection: Vector3?
        get() = definedExternally
        set(value) = definedExternally
    var sunColor: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var waterColor: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var eye: Vector3?
        get() = definedExternally
        set(value) = definedExternally
    var distortionScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var side: Any?
        get() = definedExternally
        set(value) = definedExternally
    var fog: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Water(geometry: BufferGeometry<NormalOrGLBufferAttributes>, options: WaterOptions) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
//    override var material: ShaderMaterial
}