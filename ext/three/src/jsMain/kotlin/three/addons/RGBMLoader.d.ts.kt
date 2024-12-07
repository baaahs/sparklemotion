package three.addons

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
import three.*
import kotlin.js.*

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

open external class RGBMLoader(manager: LoadingManager = definedExternally) : DataTextureLoader {
    open var type: Any
    open var maxRange: Number
    open fun loadCubemap(urls: Array<String>, onLoad: (texture: CubeTexture) -> Unit = definedExternally, onProgress: (event: ProgressEvent) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally): CubeTexture
    open fun loadCubemapAsync(urls: Array<String>, onProgress: (event: ProgressEvent) -> Unit = definedExternally): Promise<CubeTexture>
    open fun parse(buffer: ArrayBuffer): RGBM
    open fun setDataType(dataType: Any): RGBMLoader /* this */
    open fun setMaxRange(value: Number): RGBMLoader /* this */
}