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

open external class Points<TGeometry : BufferGeometry<NormalOrGLBufferAttributes>, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D<TEventMap> {
    open val isPoints: Boolean
    open var override: Any
    override val type: String /* String | "Points" */
    open var morphTargetInfluences: Array<Number>?
    open var morphTargetDictionary: `T$31`?
    open var geometry: TGeometry
    open var material: TMaterial
    open fun updateMorphTargets()
}