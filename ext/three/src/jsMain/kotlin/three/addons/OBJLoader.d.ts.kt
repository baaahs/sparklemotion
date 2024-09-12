@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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