@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

open external class OctreeHelper : LineSegments<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    constructor(octree: Octree, color: Color = definedExternally)
    constructor(octree: Octree)
    constructor(octree: Octree, color: String = definedExternally)
    constructor(octree: Octree, color: Number = definedExternally)
    open var octree: Octree
    open var color: dynamic /* Color | String | Number */
    override var type: String /* "OctreeHelper" | String */
    open fun update()
    open fun dispose()
}