@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.ErrorEvent
import three.Loader__1
import three.LoadingManager
import three.Object3D

open external class Rhino3dmLoader(manager: LoadingManager = definedExternally) : Loader__1<Object3D> {
    open fun parse(data: ArrayBuffer, onLoad: (obj: Object3D) -> Unit, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun setLibraryPath(path: String): Rhino3dmLoader
    open fun setWorkerLimit(workerLimit: Number): Rhino3dmLoader
    open fun dispose(): Rhino3dmLoader
}