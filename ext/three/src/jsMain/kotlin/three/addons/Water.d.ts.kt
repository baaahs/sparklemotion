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

external interface WaterOptions {
    var textureWidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var textureHeight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var clipBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var alpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var time: Number?
        get() = definedExternally
        set(value) = definedExternally
    var waterNormals: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var sunDirection: Vector3?
        get() = definedExternally
        set(value) = definedExternally
    var sunColor: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var waterColor: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var eye: Vector3?
        get() = definedExternally
        set(value) = definedExternally
    var distortionScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var side: Any?
        get() = definedExternally
        set(value) = definedExternally
    var fog: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external open class Water(geometry: BufferGeometry__0, options: WaterOptions) : Mesh__0 {
    override var material: ShaderMaterial
}