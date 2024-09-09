@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.w3c.dom.ErrorEvent
import org.w3c.xhr.ProgressEvent

open external class CubeTextureLoader(manager: LoadingManager = definedExternally) : Loader {
    open fun load(urls: Array<String>, onLoad: (texture: CubeTexture) -> Unit = definedExternally, onProgress: (event: ProgressEvent) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally): CubeTexture
}