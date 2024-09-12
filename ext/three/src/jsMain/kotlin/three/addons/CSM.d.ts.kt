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

external interface CSMParameters {
    var camera: Camera?
        get() = definedExternally
        set(value) = definedExternally
    var parent: Object3D__0?
        get() = definedExternally
        set(value) = definedExternally
    var cascades: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxFar: Number?
        get() = definedExternally
        set(value) = definedExternally
    var mode: String? /* "uniform" | "logarithmic" | "practical" | "custom" */
        get() = definedExternally
        set(value) = definedExternally
    var shadowMapSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var shadowBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lightDirection: Vector3?
        get() = definedExternally
        set(value) = definedExternally
    var lightIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lightNear: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lightFar: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lightMargin: Number?
        get() = definedExternally
        set(value) = definedExternally
    var customSplitsCallback: ((cascades: Number, cameraNear: Number, cameraFar: Number, breaks: Array<Number>) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

external open class CSM(data: CSMParameters) {
    open var camera: Camera
    open var parent: Object3D__0
    open var cascades: Number
    open var maxFar: Number
    open var mode: String /* "uniform" | "logarithmic" | "practical" | "custom" */
    open var shadowMapSize: Number
    open var shadowBias: Number
    open var lightDirection: Vector3
    open var lightIntensity: Number
    open var lightNear: Number
    open var lightFar: Number
    open var lightMargin: Number
    open var customSplitsCallback: (cascades: Number, cameraNear: Number, cameraFar: Number, breaks: Array<Number>) -> Unit
    open var fade: Boolean
    open var mainFrustum: CSMFrustum
    open var frustums: Array<CSMFrustum>
    open var breaks: Array<Number>
    open var lights: Array<DirectionalLight>
    open var shaders: Map<Any, String>
    open fun createLights()
    open fun initCascades()
    open fun updateShadowBounds()
    open fun getBreaks()
    open fun update()
    open fun injectInclude()
    open fun setupMaterial(material: Material)
    open fun updateUniforms()
    open fun getExtendedBreaks(target: Array<Vector2>)
    open fun updateFrustums()
    open fun remove()
    open fun dispose()
}