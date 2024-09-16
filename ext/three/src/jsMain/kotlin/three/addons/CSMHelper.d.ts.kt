@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class CSMHelper<TCSM : CSM>(csm: TCSM) : Group {
    open var csm: TCSM
    open var displayFrustum: Boolean
    open var displayPlanes: Boolean
    open var displayShadowBounds: Boolean
    open var frustumLines: LineSegments<BufferGeometry<NormalOrGLBufferAttributes>, LineBasicMaterial>
    open var cascadeLines: Array<Box3Helper>
    open var cascadePlanes: Array<Mesh<PlaneGeometry, MeshBasicMaterial>>
    open var shadowLines: Array<Box3Helper>
    open fun updateVisibility()
    open fun update()
    open fun dispose()
}