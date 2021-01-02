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

open external class PropertyMixer(binding: Any, typeName: String, valueSize: Number) {
    open var binding: Any
    open var valueSize: Number
    open var buffer: Any
    open var cumulativeWeight: Number
    open var cumulativeWeightAdditive: Number
    open var useCount: Number
    open var referenceCount: Number
    open fun accumulate(accuIndex: Number, weight: Number)
    open fun accumulateAdditive(weight: Number)
    open fun apply(accuIndex: Number)
    open fun saveOriginalState()
    open fun restoreOriginalState()
}