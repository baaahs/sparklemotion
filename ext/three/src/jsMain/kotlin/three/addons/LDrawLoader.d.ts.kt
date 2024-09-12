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

external open class LDrawLoader(manager: LoadingManager = definedExternally) : Loader__1<Group__0> {
    open var materials: Array<Material>
    open var materialsLibrary: Record<String, Material>
    open var fileMap: Record<String, String>
    open var smoothNormals: Boolean
    open fun preloadMaterials(url: String): Promise<Unit>
    open fun setFileMap(fileMap: Record<String, String>)
    open fun setMaterials(materials: Array<Material>)
    open fun parse(text: String, path: String, onLoad: (data: Group__0) -> Unit, onError: (error: Any) -> Unit = definedExternally)
    open fun addMaterial(material: Material)
    open fun getMaterial(colourCode: String): Material?
}