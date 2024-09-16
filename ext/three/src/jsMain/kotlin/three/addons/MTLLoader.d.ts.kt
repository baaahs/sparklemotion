@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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
import three.addons.MTLLoader.MaterialCreator
import kotlin.js.*

external interface MaterialCreatorOptions {
    var side: Any?
        get() = definedExternally
        set(value) = definedExternally
    var wrap: Any?
        get() = definedExternally
        set(value) = definedExternally
    var normalizeRGB: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var ignoreZeroRGBs: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var invertTrProperty: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class MTLLoader(manager: LoadingManager = definedExternally) : Loader__1<MaterialCreator> {
    open var materialOptions: MaterialCreatorOptions
    open fun parse(text: String, path: String): MaterialCreator
    open fun setMaterialOptions(value: MaterialCreatorOptions)
    interface `T$82` {
        @nativeGetter
        operator fun get(key: String): MaterialInfo?
        @nativeSetter
        operator fun set(key: String, value: MaterialInfo)
    }
    interface `T$83` {
        @nativeGetter
        operator fun get(key: String): Material?
        @nativeSetter
        operator fun set(key: String, value: Material)
    }
    open class MaterialCreator(baseUrl: String = definedExternally, options: MaterialCreatorOptions = definedExternally) {
        open var baseUrl: String
        open var options: MaterialCreatorOptions
        open var materialsInfo: `T$82`
        open var materials: `T$83`
        open var materialsArray: Array<Material>
        open var nameLookup: `T$31`
        open var side: Any
        open var wrap: Any
        open var crossOrigin: String
        open fun setCrossOrigin(value: String): MaterialCreator /* this */
        open fun setManager(value: LoadingManager)
        open fun setMaterials(materialsInfo: `T$82`)
        open fun convert(materialsInfo: `T$82`): `T$82`
        open fun preload()
        open fun getIndex(materialName: String): Number
        open fun getAsArray(): Array<Material>
        open fun create(materialName: String): Material
        open fun createMaterial_(materialName: String): Material
        open fun getTextureParams(value: String, matParams: Any): TexParams
        open fun loadTexture(url: String, mapping: Any = definedExternally, onLoad: (bufferGeometry: BufferGeometry<NormalOrGLBufferAttributes>) -> Unit = definedExternally, onProgress: (event: ProgressEvent) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally): Texture
    }
}

external interface MaterialInfo {
    var ks: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var kd: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var ke: Array<Number>?
        get() = definedExternally
        set(value) = definedExternally
    var map_kd: String?
        get() = definedExternally
        set(value) = definedExternally
    var map_ks: String?
        get() = definedExternally
        set(value) = definedExternally
    var map_ke: String?
        get() = definedExternally
        set(value) = definedExternally
    var norm: String?
        get() = definedExternally
        set(value) = definedExternally
    var map_bump: String?
        get() = definedExternally
        set(value) = definedExternally
    var bump: String?
        get() = definedExternally
        set(value) = definedExternally
    var map_d: String?
        get() = definedExternally
        set(value) = definedExternally
    var ns: Number?
        get() = definedExternally
        set(value) = definedExternally
    var d: Number?
        get() = definedExternally
        set(value) = definedExternally
    var tr: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface TexParams {
    var scale: Vector2
    var offset: Vector2
    var url: String
}