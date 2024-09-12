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

external interface CSMFrustumVerticies {
    var near: Array<Vector3>
    var far: Array<Vector3>
}

external interface CSMFrustumParameters {
    var projectionMatrix: Matrix4?
        get() = definedExternally
        set(value) = definedExternally
    var maxFar: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external open class CSMFrustum(data: CSMFrustumParameters = definedExternally) {
    open var vertices: CSMFrustumVerticies
    open fun setFromProjectionMatrix(projectionMatrix: Matrix4, maxFar: Number): CSMFrustumVerticies
    open fun split(breaks: Array<Number>, target: Array<CSMFrustum>)
    open fun toSpace(cameraMatrix: Matrix4, target: CSMFrustum)
}