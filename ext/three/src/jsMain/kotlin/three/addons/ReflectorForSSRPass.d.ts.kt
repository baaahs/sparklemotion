@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import kotlin.js.*
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

external interface `T$87` {
    var DISTANCE_ATTENUATION: Boolean
    var FRESNEL: Boolean
}

external interface ReflectorShader {
    var name: String
    var defines: `T$87`
    var uniforms: `T$17`
    var vertexShader: String
    var fragmentShader: String
}

external interface ReflectorForSSRPassOptions {
    var clipBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var textureWidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var textureHeight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var color: Number?
        get() = definedExternally
        set(value) = definedExternally
    var useDepthTexture: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var shader: ReflectorShader?
        get() = definedExternally
        set(value) = definedExternally
}

external open class ReflectorForSSRPass<TGeometry : BufferGeometry__0>(geometry: TGeometry, options: ReflectorForSSRPassOptions) : Mesh__1<TGeometry> {
    override var type: String /* "ReflectorForSSRPass" */
    open var options: ReflectorForSSRPassOptions
    open var needsUpdate: Boolean
    open var maxDistance: Number
    open var opacity: Number
    open var color: Color
    open var resolution: Vector2
    override var material: ShaderMaterial
    open var renderTarget: WebGLRenderTarget__0
    open var doRender: (renderer: WebGLRenderer, scene: Scene, camera: Camera) -> Unit
    open var getRenderTarget: () -> WebGLRenderTarget__0

    companion object {
        var ReflectorShader: ReflectorShader
    }
}

external open class ReflectorForSSRPass__0 : ReflectorForSSRPass<BufferGeometry__0>