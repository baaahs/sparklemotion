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

open external class UniformsGroup : EventDispatcher/*<`T$8`>*/ {
    open val isUniformsGroup: Boolean
    open var id: Number
    open var usage: Any
    open var uniforms: Array<dynamic /* Uniform__0 | Array<Uniform__0> */>
    open fun add(uniform: Uniform__0): UniformsGroup /* this */
    open fun add(uniform: Array<Uniform__0>): UniformsGroup /* this */
    open fun remove(uniform: Uniform__0): UniformsGroup /* this */
    open fun remove(uniform: Array<Uniform__0>): UniformsGroup /* this */
    open fun setName(name: String): UniformsGroup /* this */
    open fun setUsage(value: Any): UniformsGroup /* this */
    open fun dispose(): UniformsGroup /* this */
    open fun copy(source: UniformsGroup): UniformsGroup /* this */
    open fun clone(): UniformsGroup
}