@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.w3c.dom.ErrorEvent
import org.w3c.xhr.ProgressEvent

open external class TextureLoader(manager: LoadingManager = definedExternally) : Loader {
    open fun load(url: String, onLoad: (texture: Texture) -> Unit = definedExternally, onProgress: (event: ProgressEvent) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally): Texture
}