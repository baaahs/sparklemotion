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

external open class RenderableObject {
    open var id: Number
    open var `object`: Object3D__0
    open var z: Number
    open var renderOrder: Number
}

external open class RenderableFace {
    open var id: Number
    open var v1: RenderableVertex
    open var v2: RenderableVertex
    open var v3: RenderableVertex
    open var normalModel: Vector3
    open var vertexNormalsModel: Array<Vector3>
    open var vertexNormalsLength: Number
    open var color: Color
    open var material: Material
    open var uvs: Array<Vector2>
    open var z: Number
    open var renderOrder: Number
}

external open class RenderableVertex {
    open var position: Vector3
    open var positionWorld: Vector3
    open var positionScreen: Vector4
    open var visible: Boolean
    open fun copy(vertex: RenderableVertex)
}

external open class RenderableLine {
    open var id: Number
    open var v1: RenderableVertex
    open var v2: RenderableVertex
    open var vertexColors: Array<Color>
    open var material: Material
    open var z: Number
    open var renderOrder: Number
}

external open class RenderableSprite {
    open var id: Number
    open var `object`: Object3D__0
    open var x: Number
    open var y: Number
    open var z: Number
    open var rotation: Number
    open var scale: Vector2
    open var material: Material
    open var renderOrder: Number
}

external open class Projector {
    open fun projectScene(scene: Scene, camera: Camera, sortObjects: Boolean, sortElements: Boolean): Any
}