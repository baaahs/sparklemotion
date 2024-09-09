@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.Uint8Array

open external class AudioAnalyser(audio: Audio<AudioNode>, fftSize: Number = definedExternally) {
    open var analyser: AnalyserNode
    open var data: Uint8Array
    open fun getFrequencyData(): Uint8Array
    open fun getAverageFrequency(): Number
    open fun getData(file: Any): Any
}