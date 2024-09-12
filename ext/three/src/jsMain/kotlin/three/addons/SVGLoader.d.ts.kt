@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import js.objects.Record
import org.w3c.dom.XMLDocument
import three.*

external interface SVGResultPaths : ShapePath {
    var userData: Record<String, Any>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SVGResult {
    var paths: Array<SVGResultPaths>
    var xml: XMLDocument
}

external interface StrokeStyle {
    var strokeColor: String
    var strokeWidth: Number
    var strokeLineJoin: String
    var strokeLineCap: String
    var strokeMiterLimit: Number
}

open external class SVGLoader(manager: LoadingManager = definedExternally) : Loader__1<SVGResult> {
    open var defaultDPI: Number
    open var defaultUnit: String
    open fun parse(text: String): SVGResult

    companion object {
        fun getStrokeStyle(width: Number = definedExternally, color: String = definedExternally, lineJoin: String = definedExternally, lineCap: String = definedExternally, miterLimit: Number = definedExternally): StrokeStyle
        fun pointsToStroke(points: Array<Vector2>, style: StrokeStyle, arcDivisions: Number = definedExternally, minDistance: Number = definedExternally): BufferGeometry<NormalOrGLBufferAttributes>
        fun pointsToStrokeWithBuffers(points: Array<Vector2>, style: StrokeStyle, arcDivisions: Number = definedExternally, minDistance: Number = definedExternally, vertices: Array<Number> = definedExternally, normals: Array<Number> = definedExternally, uvs: Array<Number> = definedExternally, vertexOffset: Number = definedExternally): Number
        fun createShapes(shapePath: ShapePath): Array<Shape>
    }
}