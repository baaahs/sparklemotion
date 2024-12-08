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