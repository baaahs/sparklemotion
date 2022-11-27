package external

import dom.html.HTMLVideoElement
import kotlinx.js.HighResTimeStamp

external interface VideoFrameMetaData
typealias VideoFrameRequestCallback = (HighResTimeStamp, VideoFrameMetaData) -> Unit

inline fun HTMLVideoElement.requestVideoFrameCallback(noinline callback: VideoFrameRequestCallback): Long =
    this.asDynamic().requestVideoFrameCallback(callback)

inline fun HTMLVideoElement.cancelVideoFrameCallback(handle: Long): Unit =
    this.asDynamic().cancelVideoFrameCallback(handle)