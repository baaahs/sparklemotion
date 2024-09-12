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

external open class AfterimagePass(damp: Number = definedExternally) : Pass {
    open var shader: Any?
    open var uniforms: Record<String, IUniform__0>
    open var textureComp: WebGLRenderTarget__0
    open var textureOld: WebGLRenderTarget__0
    open var shaderMaterial: ShaderMaterial
    open var compFsQuad: FullScreenQuad
    open var copyFsQuad: FullScreenQuad
}