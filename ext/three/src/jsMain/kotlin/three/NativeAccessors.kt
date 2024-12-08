@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package three

import three.addons.GLTFLoaderPlugin
import three.addons.MTLLoader
import three.addons.MaterialInfo

inline operator fun `T$12`.get(name: String): Array<dynamic /* BufferAttribute | InterleavedBufferAttribute */>? =
    asDynamic()[name] as? Array<dynamic /* BufferAttribute | InterleavedBufferAttribute */>

inline operator fun `T$12`.set(name: String, value: Array<dynamic /* BufferAttribute | InterleavedBufferAttribute */>) {
    asDynamic()[name] = value
}

inline operator fun `T$16`.get(define: String): dynamic /* String? | Number? | Boolean? */ =
    asDynamic()[define]

inline operator fun `T$16`.set(define: String, value: String) {
    asDynamic()[define] = value
}

inline operator fun `T$16`.set(define: String, value: Number) {
    asDynamic()[define] = value
}

inline operator fun `T$16`.set(define: String, value: Boolean) {
    asDynamic()[define] = value
}

inline operator fun `T$17`.get(uniform: String): IUniform__0? =
    asDynamic()[uniform] as? IUniform__0

inline operator fun `T$17`.set(uniform: String, value: IUniform__0) {
    asDynamic()[uniform] = value
}

inline operator fun `T$31`.get(key: String): Number? =
    asDynamic()[key] as? Number

inline operator fun `T$31`.set(key: String, value: Number) {
    asDynamic()[key] = value
}

inline operator fun `T$79`.get(name: String): GLTFLoaderPlugin? =
    asDynamic()[name] as? GLTFLoaderPlugin

inline operator fun `T$79`.set(name: String, value: GLTFLoaderPlugin) {
    asDynamic()[name] = value
}

inline operator fun `T$88`.get(id: String): Array<Number>? =
    asDynamic()[id] as? Array<Number>

inline operator fun `T$88`.set(id: String, value: Array<Number>) {
    asDynamic()[id] = value
}

inline operator fun `T$89`.get(header: String): String? =
    asDynamic()[header] as? String

inline operator fun `T$89`.set(header: String, value: String) {
    asDynamic()[header] = value
}

inline operator fun `T$90`.get(key: String): Texture? =
    asDynamic()[key] as? Texture

inline operator fun `T$90`.set(key: String, value: Texture) {
    asDynamic()[key] = value
}

inline operator fun `T$91`.get(key: String): dynamic /* InstancedBufferGeometry? | BufferGeometry<NormalBufferAttributes>? */ =
    asDynamic()[key]

inline operator fun `T$91`.set(key: String, value: InstancedBufferGeometry) {
    asDynamic()[key] = value
}

inline operator fun `T$91`.set(key: String, value: BufferGeometry<NormalBufferAttributes>) {
    asDynamic()[key] = value
}

inline operator fun `T$92`.get(key: String): Material? =
    asDynamic()[key] as? Material

inline operator fun `T$92`.set(key: String, value: Material) {
    asDynamic()[key] = value
}

inline operator fun `T$93`.get(key: String): AnimationClip? =
    asDynamic()[key] as? AnimationClip

inline operator fun `T$93`.set(key: String, value: AnimationClip) {
    asDynamic()[key] = value
}

inline operator fun `T$94`.get(key: String): Source? =
    asDynamic()[key] as? Source

inline operator fun `T$94`.set(key: String, value: Source) {
    asDynamic()[key] = value
}

inline operator fun ShaderLib.get(name: String): ShaderLibShader? =
    asDynamic()[name] as? ShaderLibShader

inline operator fun ShaderLib.set(name: String, value: ShaderLibShader) {
    asDynamic()[name] = value
}

// Nested interfaces:

inline operator fun MTLLoader.`T$82`.get(key: String): MaterialInfo? =
    asDynamic()[key] as? MaterialInfo

inline operator fun MTLLoader.`T$82`.set(key: String, value: MaterialInfo) {
    asDynamic()[key] = value
}

inline operator fun MTLLoader.`T$83`.get(key: String): Material? =
    asDynamic()[key] as? Material

inline operator fun MTLLoader.`T$83`.set(key: String, value: Material) {
    asDynamic()[key] = value
}

// three.addons:

inline operator fun three.addons.`T$88`.get(name: String): `T$54`? =
    asDynamic()[name] as? `T$54`

inline operator fun three.addons.`T$88`.set(name: String, value: `T$54`) {
    asDynamic()[name] = value
}