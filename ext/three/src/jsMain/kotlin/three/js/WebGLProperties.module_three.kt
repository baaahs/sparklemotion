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

open external class WebGLProperties {
    open var has: (obj: Any) -> Boolean
    open var get: (obj: Any) -> Any
    open var remove: (obj: Any) -> Unit
    open var update: (obj: Any, key: Any, value: Any) -> Any
    open var dispose: () -> Unit
}