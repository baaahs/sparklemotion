package three.js

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
import web.audio.AudioContext
import web.audio.AudioNode
import web.audio.GainNode

open external class AudioListener : Object3D__0 {
    override val type: String /* String | "AudioListener" */
    open var context: AudioContext
    open var gain: GainNode
    open var filter: AudioNode
    open var timeDelta: Number
    open fun getInput(): GainNode
    open fun removeFilter(): AudioListener /* this */
    open fun getFilter(): AudioNode
    open fun setFilter(value: AudioNode): AudioListener /* this */
    open fun getMasterVolume(): Number
    open fun setMasterVolume(value: Number): AudioListener /* this */
}