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

external open class XRHandMeshModel(handModel: Object3D__0, controller: Object3D__0, path: String, handedness: String, loader: Loader__1<GLTF>? = definedExternally, onLoad: ((obj: Object3D__0) -> Unit)? = definedExternally) {
    open var controller: Object3D__0
    open var handModel: Object3D__0
    open var bones: Array<Object3D__0>
    open fun updateMesh()
}