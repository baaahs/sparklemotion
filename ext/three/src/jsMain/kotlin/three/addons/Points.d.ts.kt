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

external open class Points<TGeometry : BufferGeometry<NormalOrGLBufferAttributes>, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D<TEventMap> {
    open val isPoints: Boolean
    open var override: Any
    override val type: String /* String | "Points" */
    open var morphTargetInfluences: Array<Number>?
    open var morphTargetDictionary: `T$31`?
    open var geometry: TGeometry
    open var material: TMaterial
    open fun updateMorphTargets()
}

external open class Points__0 : Points<BufferGeometry__0, dynamic /* Material | Array<Material> */, Object3DEventMap>

external open class Points__2<TGeometry : BufferGeometry<NormalOrGLBufferAttributes>, TMaterial> : Points<TGeometry, TMaterial, Object3DEventMap>