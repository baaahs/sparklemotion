@file:JsModule("three")
@file:JsNonModule
package three

import org.w3c.xhr.ProgressEvent
import kotlin.js.Promise

external interface `T$91` {
    @nativeGetter
    operator fun get(key: String): dynamic /* InstancedBufferGeometry? | BufferGeometry<NormalBufferAttributes>? */
    @nativeSetter
    operator fun set(key: String, value: InstancedBufferGeometry)
    @nativeSetter
    operator fun set(key: String, value: BufferGeometry<NormalBufferAttributes>)
}

external interface `T$92` {
    @nativeGetter
    operator fun get(key: String): Material?
    @nativeSetter
    operator fun set(key: String, value: Material)
}

external interface `T$93` {
    @nativeGetter
    operator fun get(key: String): AnimationClip?
    @nativeSetter
    operator fun set(key: String, value: AnimationClip)
}

external interface `T$94` {
    @nativeGetter
    operator fun get(key: String): Source?
    @nativeSetter
    operator fun set(key: String, value: Source)
}

open external class ObjectLoader(manager: LoadingManager = definedExternally) : Loader__1<Object3D/*<Object3DEventMap>*/> {
    override fun load(url: String, onLoad: (data: Object3D/*<Object3DEventMap>*/) -> Unit, onProgress: (event: ProgressEvent) -> Unit, onError: (err: Any) -> Unit)
    open fun parse(json: Any, onLoad: (obj: Object3D/*<Object3DEventMap>*/) -> Unit = definedExternally): Object3D/*<Object3DEventMap>*/
    open fun parseAsync(json: Any): Promise<Object3D/*<Object3DEventMap>*/>
    open fun parseGeometries(json: Any): `T$91`
    open fun parseMaterials(json: Any, textures: `T$90`): `T$92`
    open fun parseAnimations(json: Any): `T$93`
    open fun parseImages(json: Any, onLoad: () -> Unit = definedExternally): `T$94`
    open fun parseImagesAsync(json: Any): Promise<`T$94`>
    open fun parseTextures(json: Any, images: `T$94`): `T$90`
    open fun parseObject(data: Any, geometries: `T$91`, materials: `T$92`, animations: `T$93`): Object3D/*<Object3DEventMap>*/
}