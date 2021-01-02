@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

open external class Skeleton(bones: Array<Bone>, boneInverses: Array<Matrix4> = definedExternally) {
    open var useVertexTexture: Boolean
    open var bones: Array<Bone>
    open var boneMatrices: Float32Array
    open var boneTexture: DataTexture?
    open var boneInverses: Array<Matrix4>
    open fun calculateInverses(bone: Bone)
    open fun pose()
    open fun update()
    open fun clone(): Skeleton
    open fun getBoneByName(name: String): Bone?
    open fun dispose()
}