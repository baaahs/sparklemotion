package three.addons

import three.Loader__1
import three.LoadingManager
import three.Scene

external interface Collada {
    var kinematics: Any?
    var library: Any?
    var scene: Scene
}

open external class ColladaLoader(manager: LoadingManager = definedExternally) : Loader__1<Collada> {
    open fun parse(text: String, path: String): Collada
}