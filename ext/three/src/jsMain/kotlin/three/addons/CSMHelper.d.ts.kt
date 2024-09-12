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

external open class CSMHelper<TCSM : CSM>(csm: TCSM) : Group__0 {
    open var csm: TCSM
    open var displayFrustum: Boolean
    open var displayPlanes: Boolean
    open var displayShadowBounds: Boolean
    open var frustumLines: LineSegments__2<BufferGeometry__0, LineBasicMaterial>
    open var cascadeLines: Array<Box3Helper>
    open var cascadePlanes: Array<Mesh__2<PlaneGeometry, MeshBasicMaterial>>
    open var shadowLines: Array<Box3Helper>
    open fun updateVisibility()
    open fun update()
    open fun dispose()
}