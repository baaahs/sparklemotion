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

open external class Layers {
    open var mask: Number
    open fun set(layer: Number)
    open fun enable(layer: Number)
    open fun enableAll()
    open fun toggle(layer: Number)
    open fun disable(layer: Number)
    open fun disableAll()
    open fun test(layers: Layers): Boolean
    open fun isEnabled(layer: Number): Boolean
}