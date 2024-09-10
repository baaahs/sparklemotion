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

open external class Line<TGeometry : BufferGeometry__0, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D<TEventMap> {
    open val isLine: Boolean
    open var override: Any
    override val type: String /* String | "Line" */
    open var geometry: TGeometry
    open var material: TMaterial
    open var morphTargetInfluences: Array<Number>?
    open var morphTargetDictionary: `T$31`?
    open fun computeLineDistances(): Line<TGeometry, TMaterial, TEventMap> /* this */
    open fun updateMorphTargets()
}

open external class Line__0 : Line<BufferGeometry__0, dynamic /* Material | Array<Material> */, Object3DEventMap>