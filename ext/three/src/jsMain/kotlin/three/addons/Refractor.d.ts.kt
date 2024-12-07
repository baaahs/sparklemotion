package three.addons

import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
import three.*
import kotlin.js.*

external interface RefractorOptions {
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

open external class Refractor(geometry: BufferGeometry<NormalOrGLBufferAttributes> = definedExternally, options: RefractorOptions = definedExternally) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, ShaderMaterial> {
    override var type: String /* "Refractor" */
    open var camera: PerspectiveCamera
    open fun getRenderTarget(): WebGLRenderTarget<*>
    open fun dispose()
}