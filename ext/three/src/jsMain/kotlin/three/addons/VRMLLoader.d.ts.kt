@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Loader__1
import three.LoadingManager
import three.Scene

open external class VRMLLoader(manager: LoadingManager = definedExternally) : Loader__1<Scene> {
    open fun parse(data: String, path: String): Scene
}