package three.addons

import org.khronos.webgl.Float32Array
import org.khronos.webgl.Int32Array
import three.*

open external class MarchingCubes(resolution: Number, material: Material, enableUvs: Boolean = definedExternally, enableColors: Boolean = definedExternally, maxPolyCount: Number = definedExternally) : Mesh<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    open var enableUvs: Boolean
    open var enableColors: Boolean
    open var resolution: Number
    open var isolation: Number
    open var size: Number
    open var size2: Number
    open var size3: Number
    open var halfsize: Number
    open var delta: Number
    open var yd: Number
    open var zd: Number
    open var field: Float32Array
    open var normal_cache: Float32Array
    open var palette: Float32Array
    open var maxCount: Number
    open var count: Number
    open var hasPositions: Boolean
    open var hasNormals: Boolean
    open var hasColors: Boolean
    open var hasUvs: Boolean
    open var positionArray: Float32Array
    open var normalArray: Float32Array
    open var uvArray: Float32Array
    open var colorArray: Float32Array
    open fun begin()
    open fun end()
    open fun init(resolution: Number)
    open fun addBall(ballx: Number, bally: Number, ballz: Number, strength: Number, subtract: Number, colors: Color = definedExternally)
    open fun addPlaneX(strength: Number, subtract: Number)
    open fun addPlaneY(strength: Number, subtract: Number)
    open fun addPlaneZ(strength: Number, subtract: Number)
    open fun setCell(x: Number, y: Number, z: Number, value: Number)
    open fun getCell(x: Number, y: Number, z: Number): Number
    open fun blur(intensity: Number)
    open fun reset()
    open fun update()
    open fun render(renderCallback: Any)
    open fun generateGeometry(): BufferGeometry<NormalOrGLBufferAttributes>
    open fun generateBufferGeometry(): BufferGeometry<NormalOrGLBufferAttributes>
}

external var edgeTable: Array<Int32Array>

external var triTable: Array<Int32Array>