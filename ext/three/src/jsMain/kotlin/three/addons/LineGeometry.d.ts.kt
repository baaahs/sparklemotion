@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Line

open external class LineGeometry : LineSegmentsGeometry {
    open val isLineGeometry: Boolean
    open fun fromLine(line: Line<*, *>): LineGeometry /* this */
}