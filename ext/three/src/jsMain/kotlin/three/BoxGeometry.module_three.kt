@file:JsModule("three")
@file:JsNonModule
package three

external interface `T$71` {
    val width: Number
    val height: Number
    val depth: Number
    val widthSegments: Number
    val heightSegments: Number
    val depthSegments: Number
}

open external class BoxGeometry(width: Number = definedExternally, height: Number = definedExternally, depth: Number = definedExternally, widthSegments: Number = definedExternally, heightSegments: Number = definedExternally, depthSegments: Number = definedExternally) : BufferGeometry<NormalBufferAttributes> {
    open var override: Any
    override val type: String /* String | "BoxGeometry" */
    open val parameters: `T$71`

    companion object {
        fun fromJSON(data: Any): BoxGeometry
    }
}