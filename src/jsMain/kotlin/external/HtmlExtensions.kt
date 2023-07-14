package external

import web.html.HTMLVideoElement

external interface VideoFrameMetaData
typealias HighResTimeStamp = Double
typealias VideoFrameRequestCallback = (HighResTimeStamp, VideoFrameMetaData) -> Unit

inline fun HTMLVideoElement.requestVideoFrameCallback(noinline callback: VideoFrameRequestCallback): Long =
    this.asDynamic().requestVideoFrameCallback(callback)

inline fun HTMLVideoElement.cancelVideoFrameCallback(handle: Long): Unit =
    this.asDynamic().cancelVideoFrameCallback(handle)