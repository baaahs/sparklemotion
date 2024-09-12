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

external interface LWO {
    var materials: Array<Material>
    var meshes: Array<Object3D__0>
}

external interface LWOLoaderParameters {
    var resourcePath: String?
        get() = definedExternally
        set(value) = definedExternally
}

external open class LWOLoader(manager: LoadingManager = definedExternally, parameters: LWOLoaderParameters = definedExternally) : Loader__1<LWO> {
    open fun parse(data: ArrayBuffer, path: String, modelName: String): LWO
}