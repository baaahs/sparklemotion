@file:JsModule("three")
@file:JsNonModule
package three.js

open external class ConeGeometry(radius: Number = definedExternally, height: Number = definedExternally, radialSegments: Number = definedExternally, heightSegments: Number = definedExternally, openEnded: Boolean = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : CylinderGeometry {
    override var override: Any
    override val type: String /* String | "ConeGeometry" */

    companion object {
        fun fromJSON(data: Any): ConeGeometry
    }
}