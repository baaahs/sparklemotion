@file:JsModule("three")
@file:JsNonModule
package three

open external class StringKeyframeTrack(name: String, times: Array<Number>, values: Array<Any>) : KeyframeTrack {
    override var ValueTypeName: String
}