package three.addons

import js.objects.Record
import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.ErrorEvent
import three.*
import kotlin.js.Json
import kotlin.js.Promise

external interface `T$77` {
    var copyright: String?
        get() = definedExternally
        set(value) = definedExternally
    var generator: String?
        get() = definedExternally
        set(value) = definedExternally
    var version: String?
        get() = definedExternally
        set(value) = definedExternally
    var minVersion: String?
        get() = definedExternally
        set(value) = definedExternally
    var extensions: Any?
        get() = definedExternally
        set(value) = definedExternally
    var extras: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface GLTF {
    var animations: Array<AnimationClip>
    var scene: Group
    var scenes: Array<Group>
    var cameras: Array<Camera>
    var asset: `T$77`
    var parser: GLTFParser
    var userData: Record<String, Any>
}

open external class GLTFLoader(manager: LoadingManager = definedExternally) : Loader__1<GLTF> {
    open var dracoLoader: DRACOLoader?
    open var ktx2Loader: KTX2Loader?
    open var meshoptDecoder: Any?
    open fun setDRACOLoader(dracoLoader: DRACOLoader): GLTFLoader /* this */
    open fun setKTX2Loader(ktx2Loader: KTX2Loader?): GLTFLoader /* this */
    open fun setMeshoptDecoder(meshoptDecoder: Any?): GLTFLoader /* this */
    open fun register(callback: (parser: GLTFParser) -> GLTFLoaderPlugin): GLTFLoader /* this */
    open fun unregister(callback: (parser: GLTFParser) -> GLTFLoaderPlugin): GLTFLoader /* this */
    open fun parse(data: ArrayBuffer, path: String, onLoad: (gltf: GLTF) -> Unit, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun parse(data: ArrayBuffer, path: String, onLoad: (gltf: GLTF) -> Unit)
    open fun parse(data: String, path: String, onLoad: (gltf: GLTF) -> Unit, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open fun parse(data: String, path: String, onLoad: (gltf: GLTF) -> Unit)
    open fun parseAsync(data: ArrayBuffer, path: String): Promise<GLTF>
    open fun parseAsync(data: String, path: String): Promise<GLTF>
}

external interface GLTFReference {
    var materials: Number?
        get() = definedExternally
        set(value) = definedExternally
    var nodes: Number?
        get() = definedExternally
        set(value) = definedExternally
    var textures: Number?
        get() = definedExternally
        set(value) = definedExternally
    var meshes: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$78` {
    var path: String
    var manager: LoadingManager
    var ktx2Loader: KTX2Loader
    var meshoptDecoder: Any
    var crossOrigin: String
    var requestHeader: `T$73`
}

external interface `T$79`

external interface `T$80` {
    var index: Number
    var texCoord: Number?
        get() = definedExternally
        set(value) = definedExternally
    var extensions: Any?
        get() = definedExternally
        set(value) = definedExternally
}

open external class GLTFParser {
    open var json: Any
    open var options: `T$78`
    open var fileLoader: FileLoader
    open var textureLoader: dynamic /* TextureLoader | ImageBitmapLoader */
    open var plugins: `T$79`
    open var extensions: Json
    open var associations: Map<dynamic /* Object3D | Material | Texture */, GLTFReference>
    open fun setExtensions(extensions: Json)
    open fun setPlugins(plugins: `T$79`)
    open fun parse(onLoad: (gltf: GLTF) -> Unit, onError: (event: ErrorEvent) -> Unit = definedExternally)
    open var getDependency: (type: String, index: Number) -> Promise<Any>
    open var getDependencies: (type: String) -> Promise<Array<Any>>
    open var loadBuffer: (bufferIndex: Number) -> Promise<ArrayBuffer>
    open var loadBufferView: (bufferViewIndex: Number) -> Promise<ArrayBuffer>
    open var loadAccessor: (accessorIndex: Number) -> Promise<dynamic /* BufferAttribute | InterleavedBufferAttribute */>
    open var loadTexture: (textureIndex: Number) -> Promise<Texture>
    open var loadTextureImage: (textureIndex: Number, sourceIndex: Number, loader: Loader__0) -> Promise<Texture>
    open var loadImageSource: (sourceIndex: Number, loader: Loader__0) -> Promise<Texture>
    open var assignTexture: (materialParams: Json, mapName: String, mapDef: `T$80`, colorSpace: Any?) -> Promise<Texture?>
    open var assignFinalMaterial: (obj: Mesh<*, *>) -> Unit
    open var getMaterialType: () -> Any
    open var loadMaterial: (materialIndex: Number) -> Promise<Material>
    open var createUniqueName: (originalName: String) -> String
    open var createNodeMesh: (nodeIndex: Number) -> Promise<dynamic /* Group | Mesh<*, *> | SkinnedMesh<*, *> */>
    open var loadGeometries: (primitives: Array<Json>) -> Promise<Array<BufferGeometry<NormalOrGLBufferAttributes>>>
    open var loadMesh: (meshIndex: Number) -> Promise<dynamic /* Group | Mesh<*, *> | SkinnedMesh<*, *> */>
    open var loadCamera: (cameraIndex: Number) -> Promise<Camera>
    open var loadSkin: (skinIndex: Number) -> Promise<Skeleton>
    open var loadAnimation: (animationIndex: Number) -> Promise<AnimationClip>
    open var loadNode: (nodeIndex: Number) -> Promise<Object3D>
    open var loadScene: () -> Promise<Group>
}

external interface GLTFLoaderPlugin {
    val name: String
    var beforeRoot: (() -> Promise<Unit>?)?
        get() = definedExternally
        set(value) = definedExternally
    var afterRoot: ((result: GLTF) -> Promise<Unit>?)?
        get() = definedExternally
        set(value) = definedExternally
    var loadNode: ((nodeIndex: Number) -> Promise<Object3D>?)?
        get() = definedExternally
        set(value) = definedExternally
    var loadMesh: ((meshIndex: Number) -> Promise<dynamic /* Group | Mesh<*, *> | SkinnedMesh<*, *> */>?)?
        get() = definedExternally
        set(value) = definedExternally
    var loadBufferView: ((bufferViewIndex: Number) -> Promise<ArrayBuffer>?)?
        get() = definedExternally
        set(value) = definedExternally
    var loadMaterial: ((materialIndex: Number) -> Promise<Material>?)?
        get() = definedExternally
        set(value) = definedExternally
    var loadTexture: ((textureIndex: Number) -> Promise<Texture>?)?
        get() = definedExternally
        set(value) = definedExternally
    var getMaterialType: ((materialIndex: Number) -> Any?)?
        get() = definedExternally
        set(value) = definedExternally
    var extendMaterialParams: ((materialIndex: Number, materialParams: Json) -> Promise<Any>?)?
        get() = definedExternally
        set(value) = definedExternally
    var createNodeMesh: ((nodeIndex: Number) -> Promise<dynamic /* Group | Mesh<*, *> | SkinnedMesh<*, *> */>?)?
        get() = definedExternally
        set(value) = definedExternally
    var createNodeAttachment: ((nodeIndex: Number) -> Promise<Object3D>?)?
        get() = definedExternally
        set(value) = definedExternally
}