package three.js

import org.w3c.dom.HTMLMediaElement
import web.audio.*
import web.media.streams.MediaStream

open external class Audio<NodeType : AudioNode>(listener: AudioListener) : Object3D__0 {
    override val type: String /* String | "Audio" */
    open var listener: AudioListener
    open var context: AudioContext
    open var gain: GainNode
    open var autoplay: Boolean
    open var buffer: AudioBuffer?
    open var detune: Number
    open var loop: Boolean
    open var loopStart: Number
    open var loopEnd: Number
    open var offset: Number
    open var duration: Number?
    open var playbackRate: Number
    open var isPlaying: Boolean
    open var hasPlaybackControl: Boolean
    open var sourceType: String
    open var source: AudioScheduledSourceNode?
    open var filters: Array<AudioNode>
    open fun getOutput(): NodeType
    open fun setNodeSource(audioNode: AudioScheduledSourceNode): Audio<NodeType> /* this */
    open fun setMediaElementSource(mediaElement: HTMLMediaElement): Audio<NodeType> /* this */
    open fun setMediaStreamSource(mediaStream: MediaStream): Audio<NodeType> /* this */
    open fun setBuffer(audioBuffer: AudioBuffer): Audio<NodeType> /* this */
    open fun play(delay: Number = definedExternally): Audio<NodeType> /* this */
    open fun pause(): Audio<NodeType> /* this */
    open fun stop(): Audio<NodeType> /* this */
    open fun onEnded()
    open fun connect(): Audio<NodeType> /* this */
    open fun disconnect(): Audio<NodeType> /* this */
    open fun getDetune(): Number
    open fun setDetune(value: Number): Audio<NodeType> /* this */
    open fun getFilter(): AudioNode
    open fun setFilter(filter: AudioNode): Audio<NodeType> /* this */
    open fun getFilters(): Array<AudioNode>
    open fun setFilters(value: Array<AudioNode>): Audio<NodeType> /* this */
    open fun getPlaybackRate(): Number
    open fun setPlaybackRate(value: Number): Audio<NodeType> /* this */
    open fun getLoop(): Boolean
    open fun setLoop(value: Boolean): Audio<NodeType> /* this */
    open fun setLoopStart(value: Number): Audio<NodeType> /* this */
    open fun setLoopEnd(value: Number): Audio<NodeType> /* this */
    open fun getVolume(): Number
    open fun setVolume(value: Number): Audio<NodeType> /* this */
}