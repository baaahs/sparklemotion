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

open external class Audio<NodeType : AudioNode>(listener: AudioListener) : Object3D {
    override var type: String /* 'Audio' */
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
    open var source: AudioBufferSourceNode?
    open var filters: Array<Any>
    open fun getOutput(): NodeType
    open fun setNodeSource(audioNode: AudioBufferSourceNode): Audio<NodeType> /* this */
    open fun setMediaElementSource(mediaElement: HTMLMediaElement): Audio<NodeType> /* this */
    open fun setMediaStreamSource(mediaStream: MediaStream): Audio<NodeType> /* this */
    open fun setBuffer(audioBuffer: AudioBuffer): Audio<NodeType> /* this */
    open fun play(delay: Number = definedExternally): Audio<NodeType> /* this */
    open fun onEnded()
    open fun pause(): Audio<NodeType> /* this */
    open fun stop(): Audio<NodeType> /* this */
    open fun connect(): Audio<NodeType> /* this */
    open fun disconnect(): Audio<NodeType> /* this */
    open fun setDetune(value: Number): Audio<NodeType> /* this */
    open fun getDetune(): Number
    open fun getFilters(): Array<Any>
    open fun setFilters(value: Array<Any>): Audio<NodeType> /* this */
    open fun getFilter(): Any
    open fun setFilter(filter: Any): Audio<NodeType> /* this */
    open fun setPlaybackRate(value: Number): Audio<NodeType> /* this */
    open fun getPlaybackRate(): Number
    open fun getLoop(): Boolean
    open fun setLoop(value: Boolean): Audio<NodeType> /* this */
    open fun setLoopStart(value: Number): Audio<NodeType> /* this */
    open fun setLoopEnd(value: Number): Audio<NodeType> /* this */
    open fun getVolume(): Number
    open fun setVolume(value: Number): Audio<NodeType> /* this */
}