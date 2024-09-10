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

open external class SkeletonHelper : LineSegments__0 {
    constructor(obj: SkinnedMesh__0)
    constructor(obj: Object3D__0)
    open val isSkeletonHelper: Any = definedExternally /* true */
    override var override: Any
    override val type: String /* String | "SkeletonHelper" */
    open var bones: Array<Bone__0>
    open var root: dynamic /* SkinnedMesh__0 | Object3D__0 */
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open fun update()
    open fun dispose()
}