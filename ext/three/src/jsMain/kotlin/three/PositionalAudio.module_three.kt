@file:JsModule("three")
@file:JsNonModule
package three

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
import web.audio.PannerNode

open external class PositionalAudio(listener: AudioListener) : Audio<PannerNode> {
    open var panner: PannerNode
    override fun getOutput(): PannerNode
    open fun getRefDistance(): Number
    open fun setRefDistance(value: Number): PositionalAudio /* this */
    open fun getRolloffFactor(): Number
    open fun setRolloffFactor(value: Number): PositionalAudio /* this */
    open fun getDistanceModel(): String
    open fun setDistanceModel(value: String): PositionalAudio /* this */
    open fun getMaxDistance(): Number
    open fun setMaxDistance(value: Number): PositionalAudio /* this */
    open fun setDirectionalCone(coneInnerAngle: Number, coneOuterAngle: Number, coneOuterGain: Number): PositionalAudio /* this */
}