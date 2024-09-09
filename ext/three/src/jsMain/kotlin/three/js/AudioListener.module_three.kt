@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class AudioListener : Object3D {
    override var type: String /* 'AudioListener' */
    open var context: AudioContext
    open var gain: GainNode
    open var filter: Any?
    open var timeDelta: Number
    open fun getInput(): GainNode
    open fun removeFilter(): AudioListener /* this */
    open fun setFilter(value: Any): AudioListener /* this */
    open fun getFilter(): Any
    open fun setMasterVolume(value: Number): AudioListener /* this */
    open fun getMasterVolume(): Number
    override fun updateMatrixWorld(force: Boolean)
}