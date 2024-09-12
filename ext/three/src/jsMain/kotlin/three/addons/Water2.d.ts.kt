@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

external interface Water2Options {
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var textureWidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var textureHeight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clipBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var flowDirection: Vector2?
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
    var shader: Any?
        get() = definedExternally
        set(value) = definedExternally
    var flowMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var normalMap0: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var normalMap1: Texture?
        get() = definedExternally
        set(value) = definedExternally
}

@JsName("Water")
open external class Water2(geometry: BufferGeometry<NormalOrGLBufferAttributes>, options: WaterOptions) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
//    override var material: ShaderMaterial
}