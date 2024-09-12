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

external interface `T$88` {
    @nativeGetter
    operator fun get(name: String): `T$54`?
    @nativeSetter
    operator fun set(name: String, value: `T$54`)
}

external open class ShaderPass(shader: Any?, textureID: String = definedExternally) : Pass {
    open var textureID: String
    open var uniforms: `T$88`
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
}