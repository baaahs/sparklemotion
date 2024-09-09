@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.w3c.xhr.ProgressEvent

external interface `T$46` {
    @nativeGetter
    operator fun get(key: String): Texture?
    @nativeSetter
    operator fun set(key: String, value: Texture)
}

open external class MaterialLoader(manager: LoadingManager = definedExternally) : Loader {
    open var textures: `T$46`
    open fun load(url: String, onLoad: (material: Material) -> Unit, onProgress: (event: ProgressEvent) -> Unit = definedExternally, onError: (event: dynamic /* Error | ErrorEvent */) -> Unit = definedExternally)
    open fun setTextures(textures: `T$46`): MaterialLoader /* this */
    open fun parse(json: Any): Material
}