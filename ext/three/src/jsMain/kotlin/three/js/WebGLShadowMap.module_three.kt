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

open external class WebGLShadowMap(_renderer: WebGLRenderer, _objects: WebGLObjects, _capabilities: WebGLCapabilities) {
    open var enabled: Boolean
    open var autoUpdate: Boolean
    open var needsUpdate: Boolean
    open var type: Any
    open fun render(shadowsArray: Array<Light__0>, scene: Scene, camera: Camera)
    open var cullFace: Any
}