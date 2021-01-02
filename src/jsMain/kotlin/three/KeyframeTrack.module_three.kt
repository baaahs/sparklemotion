@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

open external class KeyframeTrack(name: String, times: Array<Any>, values: Array<Any>, interpolation: InterpolationModes = definedExternally) {
    open var name: String
    open var times: Float32Array
    open var values: Float32Array
    open var ValueTypeName: String
    open var TimeBufferType: Float32Array
    open var ValueBufferType: Float32Array
    open var DefaultInterpolation: InterpolationModes
    open fun InterpolantFactoryMethodDiscrete(result: Any): DiscreteInterpolant
    open fun InterpolantFactoryMethodLinear(result: Any): LinearInterpolant
    open fun InterpolantFactoryMethodSmooth(result: Any): CubicInterpolant
    open fun setInterpolation(interpolation: InterpolationModes): KeyframeTrack
    open fun getInterpolation(): InterpolationModes
    open fun getValueSize(): Number
    open fun shift(timeOffset: Number): KeyframeTrack
    open fun scale(timeScale: Number): KeyframeTrack
    open fun trim(startTime: Number, endTime: Number): KeyframeTrack
    open fun validate(): Boolean
    open fun optimize(): KeyframeTrack
    open fun clone(): KeyframeTrack

    companion object {
        fun toJSON(track: KeyframeTrack): Any
    }
}