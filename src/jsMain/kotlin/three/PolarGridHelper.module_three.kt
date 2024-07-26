@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class PolarGridHelper : LineSegments<dynamic, dynamic> {
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Color?, color2: Color?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Color?, color2: String?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Color?, color2: Number?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: String?, color2: Color?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: String?, color2: String?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: String?, color2: Number?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Number?, color2: Color?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Number?, color2: String?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Number?, color2: Number?)
    override var type: String
}