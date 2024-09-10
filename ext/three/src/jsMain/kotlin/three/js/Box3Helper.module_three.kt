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

open external class Box3Helper : LineSegments__0 {
    constructor(box: Box3, color: Color = definedExternally)
    constructor(box: Box3)
    constructor(box: Box3, color: String = definedExternally)
    constructor(box: Box3, color: Number = definedExternally)
    override var override: Any
    override val type: String /* String | "Box3Helper" */
    open var box: Box3
    open fun dispose()
}