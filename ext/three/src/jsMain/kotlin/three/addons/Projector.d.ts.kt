package three.addons

import three.*

open external class RenderableObject {
    open var id: Number
    open var `object`: Object3D
    open var z: Number
    open var renderOrder: Number
}

open external class RenderableFace {
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

open external class RenderableVertex {
    open var position: Vector3
    open var positionWorld: Vector3
    open var positionScreen: Vector4
    open var visible: Boolean
    open fun copy(vertex: RenderableVertex)
}

open external class RenderableLine {
    open var id: Number
    open var v1: RenderableVertex
    open var v2: RenderableVertex
    open var vertexColors: Array<Color>
    open var material: Material
    open var z: Number
    open var renderOrder: Number
}

open external class RenderableSprite {
    open var id: Number
    open var `object`: Object3D
    open var x: Number
    open var y: Number
    open var z: Number
    open var rotation: Number
    open var scale: Vector2
    open var material: Material
    open var renderOrder: Number
}

open external class Projector {
    open fun projectScene(scene: Scene, camera: Camera, sortObjects: Boolean, sortElements: Boolean): Any
}