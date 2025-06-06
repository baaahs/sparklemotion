package three.addons

import js.objects.Record
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
import three.*
import kotlin.js.*

open external class TexturePass(map: Texture = definedExternally, opacity: Number = definedExternally) : Pass {
    open var map: Texture?
    open var opacity: Number
    open var uniforms: Record<String, IUniform__0>
    open var material: ShaderMaterial
    open var fsQuad: FullScreenQuad
}