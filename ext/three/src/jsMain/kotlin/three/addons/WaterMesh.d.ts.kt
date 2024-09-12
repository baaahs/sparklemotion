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

external interface WaterMeshOptions {
    var resolution: Number?
        get() = definedExternally
        set(value) = definedExternally
    var waterNormals: Any
    var alpha: Number?
        get() = definedExternally
        set(value) = definedExternally
    var size: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sunColor: Any?
        get() = definedExternally
        set(value) = definedExternally
    var sunDirection: Any?
        get() = definedExternally
        set(value) = definedExternally
    var waterColor: Any?
        get() = definedExternally
        set(value) = definedExternally
    var distortionScale: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external open class WaterMesh(geometry: BufferGeometry, options: WaterMeshOptions) : Mesh<BufferGeometry, NodeMaterial> {
    open val isWater: Boolean
    open var resolution: Number
    open var waterNormals: Any
    open var alpha: Any
    open var size: Any
    open var sunColor: Any
    open var sunDirection: Any
    open var waterColor: Any
    open var distortionScale: Any
}