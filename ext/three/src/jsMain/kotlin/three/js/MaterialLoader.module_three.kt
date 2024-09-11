@file:JsModule("three")
@file:JsNonModule
package three.js

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

external interface `T$90` {
    @nativeGetter
    operator fun get(key: String): Texture?
    @nativeSetter
    operator fun set(key: String, value: Texture)
}

open external class MaterialLoader(manager: LoadingManager = definedExternally) : Loader__1<Material> {
    open var textures: `T$90`
    open fun parse(json: Any): Material
    open fun setTextures(textures: `T$90`): MaterialLoader /* this */

    companion object {
        fun createMaterialFromType(type: String): Material
    }
}