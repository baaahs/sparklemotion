@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external open class Timer {
    open fun getDelta(): Number
    open fun getElapsed(): Number
    open fun getTimescale(): Number
    open fun setTimescale(timescale: Number): Timer /* this */
    open fun reset(): Timer /* this */
    open fun dispose(): Timer /* this */
    open fun update(timestamp: Number = definedExternally): Timer /* this */
}

external open class FixedTimer(fps: Number = definedExternally) : Timer {
    open var override: Any
    open fun update(): FixedTimer /* this */
}