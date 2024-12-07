package three.addons

import three.*

external interface SplineUniform {
    var spineTexture: IUniform__0
    var pathOffset: IUniform__0
    var pathSegment: IUniform__0
    var spineOffset: IUniform__0
    var flow: IUniform__0
}

external fun initSplineTexture(size: Number = definedExternally): DataTexture

external fun updateSplineTexture(texture: DataTexture, splineCurve: Curve, offset: Number = definedExternally)

external fun getUniforms(splineTexture: DataTexture): SplineUniform

external fun modifyShader(material: Material, uniforms: SplineUniform, numberOfCurves: Number = definedExternally)

open external class Flow(mesh: Mesh<*, *>, numberOfCurves: Number = definedExternally) {
    open var curveArray: Array<Number>
    open var curveLengthArray: Array<Number>
    open var object3D: Mesh<*, *>
    open var splineTexure: DataTexture
    open var uniforms: SplineUniform
    open fun updateCurve(index: Number, curve: Curve)
    open fun moveAlongCurve(amount: Number)
}

open external class InstancedFlow(count: Number, curveCount: Number, geometry: BufferGeometry<NormalOrGLBufferAttributes>, material: Material) : Flow {
    open var offsets: Array<Number>
    open var whichCurve: Array<Number>
    open fun moveIndividualAlongCurve(index: Number, offset: Number)
    open fun setCurve(index: Number, curveNo: Number)
}