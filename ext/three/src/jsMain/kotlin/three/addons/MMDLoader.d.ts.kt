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

external interface MMDLoaderAnimationObject {
    var animation: AnimationClip
    var mesh: SkinnedMesh__0
}

external open class MMDLoader(manager: LoadingManager = definedExternally) : Loader__1<SkinnedMesh__0> {
    open var animationBuilder: Any?
    open var animationPath: String
    open var loader: FileLoader
    open var meshBuilder: Any?
    open var parser: Any?
    open fun loadAnimation(url: String, obj: SkinnedMesh__0, onLoad: (obj: Any /* SkinnedMesh__0 | AnimationClip */) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun loadAnimation(url: String, obj: SkinnedMesh__0, onLoad: (obj: Any /* SkinnedMesh__0 | AnimationClip */) -> Unit)
    open fun loadAnimation(url: String, obj: SkinnedMesh__0, onLoad: (obj: Any /* SkinnedMesh__0 | AnimationClip */) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally)
    open fun loadAnimation(url: String, obj: Camera, onLoad: (obj: Any /* SkinnedMesh__0 | AnimationClip */) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun loadAnimation(url: String, obj: Camera, onLoad: (obj: Any /* SkinnedMesh__0 | AnimationClip */) -> Unit)
    open fun loadAnimation(url: String, obj: Camera, onLoad: (obj: Any /* SkinnedMesh__0 | AnimationClip */) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally)
    open fun loadPMD(url: String, onLoad: (obj: Any?) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun loadPMX(url: String, onLoad: (obj: Any?) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun loadVMD(url: String, onLoad: (obj: Any?) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun loadVPD(url: String, isUnicode: Boolean, onLoad: (obj: Any?) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun loadWithAnimation(url: String, vmdUrl: String, onLoad: (obj: MMDLoaderAnimationObject) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun loadWithAnimation(url: String, vmdUrl: String, onLoad: (obj: MMDLoaderAnimationObject) -> Unit)
    open fun loadWithAnimation(url: String, vmdUrl: String, onLoad: (obj: MMDLoaderAnimationObject) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally)
    open fun loadWithAnimation(url: String, vmdUrl: Array<String>, onLoad: (obj: MMDLoaderAnimationObject) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun loadWithAnimation(url: String, vmdUrl: Array<String>, onLoad: (obj: MMDLoaderAnimationObject) -> Unit)
    open fun loadWithAnimation(url: String, vmdUrl: Array<String>, onLoad: (obj: MMDLoaderAnimationObject) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally)
    open fun setAnimationPath(animationPath: String): MMDLoader /* this */
}