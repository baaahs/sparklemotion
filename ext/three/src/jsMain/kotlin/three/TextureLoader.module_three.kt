@file:JsModule("three")
@file:JsNonModule
package three

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

open external class TextureLoader(manager: LoadingManager = definedExternally) : Loader__1<Texture> {
    override fun load(url: String, onLoad: (data: Texture) -> Unit, onProgress: (event: ProgressEvent) -> Unit, onError: (err: Any) -> Unit): Texture
}