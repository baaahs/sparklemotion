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

open external class Interpolant(parameterPositions: Any, sampleValues: Any, sampleSize: Number, resultBuffer: Any = definedExternally) {
    open var parameterPositions: Any
    open var sampleValues: Any
    open var valueSize: Number
    open var resultBuffer: Any
    open fun evaluate(time: Number): Any
}