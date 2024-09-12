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

external interface RGBM {
    var width: Number
    var height: Number
    var data: dynamic /* Uint16Array | Float32Array */
        get() = definedExternally
        set(value) = definedExternally
    var header: String
    var format: Any
    var type: Any
    var flipY: Boolean
}

external open class RGBMLoader(manager: LoadingManager = definedExternally) : DataTextureLoader {
    open var type: Any
    open var maxRange: Number
    open fun loadCubemap(urls: Array<String>, onLoad: (texture: CubeTexture) -> Unit = definedExternally, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally): CubeTexture
    open fun loadCubemapAsync(urls: Array<String>, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally): Promise<CubeTexture>
    open fun parse(buffer: ArrayBuffer): RGBM
    open fun setDataType(dataType: Any): RGBMLoader /* this */
    open fun setMaxRange(value: Number): RGBMLoader /* this */
}