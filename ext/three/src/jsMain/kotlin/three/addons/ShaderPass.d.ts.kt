@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.ShaderMaterial
import three.`T$54`

external interface `T$88` {
    @nativeGetter
    operator fun get(name: String): `T$54`?
    @nativeSetter
    operator fun set(name: String, value: `T$54`)
}

open external class ShaderPass(shader: Any?, textureID: String = definedExternally) : Pass {
    open var textureID: String
    open var uniforms: `T$88`
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
}