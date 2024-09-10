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

open external class CubicInterpolant(parameterPositions: Any, samplesValues: Any, sampleSize: Number, resultBuffer: Any = definedExternally) : Interpolant {
    open fun interpolate_(i1: Number, t0: Number, t: Number, t1: Number): Any
}