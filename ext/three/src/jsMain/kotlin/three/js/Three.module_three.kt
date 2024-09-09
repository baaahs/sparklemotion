@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.ArrayBufferView

typealias GLenum = Number

typealias TypedArray = ArrayBufferView

external interface AudioNode

external interface GainNode : AudioNode

external interface PannerNode : AudioNode

external interface AudioContext

external interface AudioBuffer

external interface AudioBufferSourceNode

external interface MediaStream

external interface AnalyserNode

external interface ArrayLike<T>

external interface WebGL2RenderingContext