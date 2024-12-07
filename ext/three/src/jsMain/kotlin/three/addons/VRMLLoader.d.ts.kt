package three.addons

import three.Loader__1
import three.LoadingManager
import three.Scene

open external class VRMLLoader(manager: LoadingManager = definedExternally) : Loader__1<Scene> {
    open fun parse(data: String, path: String): Scene
}