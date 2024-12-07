package three.addons

import three.*

external interface ReflectorOptions {
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
    var shader: Any?
        get() = definedExternally
        set(value) = definedExternally
    var multisample: Number?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Reflector(geometry: BufferGeometry<NormalOrGLBufferAttributes> = definedExternally, options: ReflectorOptions = definedExternally) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    override var type: String /* "Reflector" */
    open var camera: PerspectiveCamera
    open fun getRenderTarget(): WebGLRenderTarget<*>
    open fun dispose()
}