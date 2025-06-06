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

open external class ShadowMesh(mesh: Mesh<*, *>) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, MeshBasicMaterial> {
    open val isShadowMesh: Boolean
    open var meshMatrix: Matrix4
    open fun update(plane: Plane, lightPosition4D: Vector4)
}