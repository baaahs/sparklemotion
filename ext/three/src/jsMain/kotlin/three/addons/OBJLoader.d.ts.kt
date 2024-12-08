package three.addons

import three.Group
import three.Loader__1
import three.LoadingManager
import three.addons.MTLLoader.MaterialCreator

open external class OBJLoader(manager: LoadingManager = definedExternally) : Loader__1<Group> {
    open var materials: MaterialCreator
    open fun parse(data: String): Group
    open fun setMaterials(materials: MaterialCreator): OBJLoader /* this */
}