@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.w3c.dom.HTMLImageElement
import org.w3c.xhr.ProgressEvent

external interface `T$45` {
    @nativeGetter
    operator fun get(key: String): HTMLImageElement?
    @nativeSetter
    operator fun set(key: String, value: HTMLImageElement)
}

open external class ObjectLoader(manager: LoadingManager = definedExternally) : Loader {
    open fun <ObjectType: Object3D> load(url: String, onLoad: (obj: ObjectType) -> Unit = definedExternally, onProgress: (event: ProgressEvent) -> Unit = definedExternally, onError: (event: dynamic /* Error | ErrorEvent */) -> Unit = definedExternally)
    open fun <T : Object3D> parse(json: Any, onLoad: (obj: Object3D) -> Unit = definedExternally): T
    open fun parseGeometries(json: Any): Array<Any>
    open fun parseMaterials(json: Any, textures: Array<Texture>): Array<Material>
    open fun parseAnimations(json: Any): Array<AnimationClip>
    open fun parseImages(json: Any, onLoad: () -> Unit): `T$45`
    open fun parseTextures(json: Any, images: Any): Array<Texture>
    open fun <T : Object3D> parseObject(data: Any, geometries: Array<Any>, materials: Array<Material>): T
}