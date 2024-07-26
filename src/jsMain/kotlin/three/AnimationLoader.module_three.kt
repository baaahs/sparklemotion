@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.w3c.dom.ErrorEvent
import org.w3c.xhr.ProgressEvent

open external class AnimationLoader(manager: LoadingManager = definedExternally) : Loader {
    open fun load(url: String, onLoad: (response: Array<AnimationClip>) -> Unit, onProgress: (request: ProgressEvent) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun parse(json: Any): Array<AnimationClip>
}