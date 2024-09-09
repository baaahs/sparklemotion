@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Box3Helper(box: Box3, color: Color = definedExternally) : LineSegments<dynamic, dynamic> {
    override var type: String
    open var box: Box3
}