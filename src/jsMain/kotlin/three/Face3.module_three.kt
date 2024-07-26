@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Face3 {
    constructor(a: Int, b: Int, c: Int, normal: Vector3 = definedExternally, color: Color = definedExternally, materialIndex: Int = definedExternally)
    constructor(a: Int, b: Int, c: Int, normal: Vector3 = definedExternally, vertexColors: Array<Color> = definedExternally, materialIndex: Int = definedExternally)
    constructor(a: Int, b: Int, c: Int, vertexNormals: Array<Vector3> = definedExternally, color: Color = definedExternally, materialIndex: Int = definedExternally)
    constructor(a: Int, b: Int, c: Int, vertexNormals: Array<Vector3> = definedExternally, vertexColors: Array<Color> = definedExternally, materialIndex: Int = definedExternally)
    open var a: Int
    open var b: Int
    open var c: Int
    open var normal: Vector3
    open var vertexNormals: Array<Vector3>
    open var color: Color
    open var vertexColors: Array<Color>
    open var materialIndex: Int
    open fun clone(): Face3 /* this */
    open fun copy(source: Face3): Face3 /* this */
}