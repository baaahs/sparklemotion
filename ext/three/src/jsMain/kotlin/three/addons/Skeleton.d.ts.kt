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

external interface SkeletonJSON {
    var metadata: `T$0`
    var bones: Array<String>
    var boneInverses: Array<dynamic /* JsTuple<n11, Number, n12, Number, n13, Number, n14, Number, n21, Number, n22, Number, n23, Number, n24, Number, n31, Number, n32, Number, n33, Number, n34, Number, n41, Number, n42, Number, n43, Number, n44, Number> */>
    var uuid: String
}

external open class Skeleton(bones: Array<Bone__0> = definedExternally, boneInverses: Array<Matrix4> = definedExternally) {
    open var uuid: String
    open var bones: Array<Bone__0>
    open var boneInverses: Array<Matrix4>
    open var boneMatrices: Float32Array
    open var boneTexture: DataTexture?
    open var frame: Number
    open fun init()
    open fun calculateInverses()
    open fun computeBoneTexture(): Skeleton /* this */
    open fun pose()
    open fun update()
    open fun clone(): Skeleton
    open fun getBoneByName(name: String): Bone__0?
    open fun dispose()
    open fun toJSON(): SkeletonJSON
    open fun fromJSON(json: SkeletonJSON, bones: Record<String, Bone__0>)
}