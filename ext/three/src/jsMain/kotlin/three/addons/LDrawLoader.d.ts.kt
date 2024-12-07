package three.addons

import js.objects.Record
import three.Group
import three.Loader__1
import three.LoadingManager
import three.Material
import kotlin.js.Promise

open external class LDrawLoader(manager: LoadingManager = definedExternally) : Loader__1<Group> {
    open var materials: Array<Material>
    open var materialsLibrary: Record<String, Material>
    open var fileMap: Record<String, String>
    open var smoothNormals: Boolean
    open fun preloadMaterials(url: String): Promise<Unit>
    open fun setFileMap(fileMap: Record<String, String>)
    open fun setMaterials(materials: Array<Material>)
    open fun parse(text: String, path: String, onLoad: (data: Group) -> Unit, onError: (error: Any) -> Unit = definedExternally)
    open fun addMaterial(material: Material)
    open fun getMaterial(colourCode: String): Material?
}