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

open external class Clock(autoStart: Boolean = definedExternally) {
    open var autoStart: Boolean
    open var startTime: Number
    open var oldTime: Number
    open var elapsedTime: Number
    open var running: Boolean
    open fun start()
    open fun stop()
    open fun getElapsedTime(): Number
    open fun getDelta(): Number
}