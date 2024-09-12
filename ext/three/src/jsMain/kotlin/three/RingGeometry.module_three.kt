@file:JsModule("three")
@file:JsNonModule
package three

external interface `T$81` {
    val innerRadius: Number
    val outerRadius: Number
    val thetaSegments: Number
    val phiSegments: Number
    val thetaStart: Number
    val thetaLength: Number
}

open external class RingGeometry(innerRadius: Number = definedExternally, outerRadius: Number = definedExternally, thetaSegments: Number = definedExternally, phiSegments: Number = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : BufferGeometry<NormalBufferAttributes> {
    open var override: Any
    override val type: String /* String | "RingGeometry" */
    open val parameters: `T$81`

    companion object {
        fun fromJSON(data: Any): RingGeometry
    }
}