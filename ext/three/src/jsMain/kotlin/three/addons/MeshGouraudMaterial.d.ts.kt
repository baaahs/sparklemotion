@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Material
import three.ShaderMaterial
import three.ShaderMaterialParameters

open external class MeshGouraudMaterial(parameters: ShaderMaterialParameters = definedExternally) : ShaderMaterial {
    open var isMeshGouraudMaterial: Boolean
    override var type: String /* "MeshGouraudMaterial" */
    open fun copy(source: MeshGouraudMaterial): MeshGouraudMaterial /* this */
    override fun copy(material: Material): Material /* this */
}