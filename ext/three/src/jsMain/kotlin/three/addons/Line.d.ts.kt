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

external open class Line<TGeometry : BufferGeometry__0, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D<TEventMap> {
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

external open class Line__0 : Line<BufferGeometry__0, dynamic /* Material | Array<Material> */, Object3DEventMap>