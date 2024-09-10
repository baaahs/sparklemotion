@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

import js.array.ArrayLike
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

external interface KeyframeTrackJSON {
    var name: String
    var times: Array<Number>
    var values: Array<Number>
    var interpolation: Any?
        get() = definedExternally
        set(value) = definedExternally
    var type: String
}

open external class KeyframeTrack(name: String, times: ArrayLike<Number>, values: ArrayLike<Any>, interpolation: Any = definedExternally) {
    open var name: String
    open var times: Float32Array
    open var values: Float32Array
    open var ValueTypeName: String
    open var TimeBufferType: Float32Array
    open var ValueBufferType: Float32Array
    open var DefaultInterpolation: Any
    open fun InterpolantFactoryMethodDiscrete(result: Any): DiscreteInterpolant
    open fun InterpolantFactoryMethodLinear(result: Any): LinearInterpolant
    open fun InterpolantFactoryMethodSmooth(result: Any): CubicInterpolant
    open fun setInterpolation(interpolation: Any): KeyframeTrack
    open fun getInterpolation(): Any
    open fun createInterpolant(): Interpolant
    open fun getValueSize(): Number
    open fun shift(timeOffset: Number): KeyframeTrack
    open fun scale(timeScale: Number): KeyframeTrack
    open fun trim(startTime: Number, endTime: Number): KeyframeTrack
    open fun validate(): Boolean
    open fun optimize(): KeyframeTrack
    open fun clone(): KeyframeTrack /* this */

    companion object {
        fun toJSON(track: KeyframeTrack): KeyframeTrackJSON
    }
}