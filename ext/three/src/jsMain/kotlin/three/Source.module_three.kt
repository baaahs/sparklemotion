@file:JsModule("three")
@file:JsNonModule
package three

open external class SourceJSON {
    open var uuid: String
    open var url: dynamic /* String | `T$18` | Array<dynamic /* String | `T$18` */> */
}

open external class Source(data: Any) {
    open val isSource: Boolean
    open val id: Number
    open var data: Any
    open var dataReady: Boolean
    open var uuid: String
    open var version: Number
    open fun toJSON(meta: String = definedExternally): SourceJSON
    open fun toJSON(): SourceJSON
    open fun toJSON(meta: Any = definedExternally): SourceJSON
}