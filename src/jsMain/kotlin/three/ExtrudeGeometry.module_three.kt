@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface ExtrudeGeometryOptions {
    var curveSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
    var steps: Number?
        get() = definedExternally
        set(value) = definedExternally
    var depth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var bevelThickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelOffset: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
    var extrudePath: Curve<Vector3>?
        get() = definedExternally
        set(value) = definedExternally
    var UVGenerator: UVGenerator?
        get() = definedExternally
        set(value) = definedExternally
}

external interface UVGenerator {
    fun generateTopUV(geometry: ExtrudeBufferGeometry, vertices: Array<Number>, indexA: Number, indexB: Number, indexC: Number): Array<Vector2>
    fun generateSideWallUV(geometry: ExtrudeBufferGeometry, vertices: Array<Number>, indexA: Number, indexB: Number, indexC: Number, indexD: Number): Array<Vector2>
}

open external class ExtrudeBufferGeometry : BufferGeometry {
    constructor(shapes: Shape, options: ExtrudeGeometryOptions = definedExternally)
    constructor(shapes: Array<Shape>, options: ExtrudeGeometryOptions = definedExternally)
    override var type: String
    open fun addShapeList(shapes: Array<Shape>, options: Any = definedExternally)
    open fun addShape(shape: Shape, options: Any = definedExternally)

    companion object {
        var WorldUVGenerator: UVGenerator
    }
}

open external class ExtrudeGeometry : Geometry {
    constructor(shapes: Shape, options: ExtrudeGeometryOptions = definedExternally)
    constructor(shapes: Array<Shape>, options: ExtrudeGeometryOptions = definedExternally)
    override var type: String
    open fun addShapeList(shapes: Array<Shape>, options: Any = definedExternally)
    open fun addShape(shape: Shape, options: Any = definedExternally)

    companion object {
        var WorldUVGenerator: UVGenerator
    }
}