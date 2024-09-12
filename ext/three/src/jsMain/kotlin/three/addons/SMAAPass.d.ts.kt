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

external open class SMAAPass(width: Number, height: Number) : Pass {
    open var edgesRT: WebGLRenderTarget__0
    open var weightsRT: WebGLRenderTarget__0
    open var areaTexture: Texture
    open var searchTexture: Texture
    open var uniformsEdges: Any?
    open var materialEdges: ShaderMaterial
    open var uniformsWeights: Any?
    open var materialWeights: ShaderMaterial
    open var uniformsBlend: Any?
    open var materialBlend: ShaderMaterial
    open var fsQuad: FullScreenQuad
    open fun getAreaTexture(): String
    open fun getSearchTexture(): String
}