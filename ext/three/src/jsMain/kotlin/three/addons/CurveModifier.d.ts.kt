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

external interface SplineUniform {
    var spineTexture: IUniform__0
    var pathOffset: IUniform__0
    var pathSegment: IUniform__0
    var spineOffset: IUniform__0
    var flow: IUniform__0
}

external fun initSplineTexture(size: Number = definedExternally): DataTexture

external fun updateSplineTexture(texture: DataTexture, splineCurve: Curve<Vector3>, offset: Number = definedExternally)

external fun getUniforms(splineTexture: DataTexture): SplineUniform

external fun modifyShader(material: Material, uniforms: SplineUniform, numberOfCurves: Number = definedExternally)

external open class Flow(mesh: Mesh__0, numberOfCurves: Number = definedExternally) {
    open var curveArray: Array<Number>
    open var curveLengthArray: Array<Number>
    open var object3D: Mesh__0
    open var splineTexure: DataTexture
    open var uniforms: SplineUniform
    open fun updateCurve(index: Number, curve: Curve<Vector3>)
    open fun moveAlongCurve(amount: Number)
}

external open class InstancedFlow(count: Number, curveCount: Number, geometry: BufferGeometry__0, material: Material) : Flow {
    open var offsets: Array<Number>
    open var whichCurve: Array<Number>
    open fun moveIndividualAlongCurve(index: Number, offset: Number)
    open fun setCurve(index: Number, curveNo: Number)
}