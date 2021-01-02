@file:JsModule("three")
@file:JsNonModule()
package three_ext

import three.js.BufferAttribute

open external class Float32BufferAttribute(
    array: dynamic,
    itemSize: Int,
    normalized: Boolean = definedExternally
) : BufferAttribute

open external class Matrix4 : three.js.Matrix4
