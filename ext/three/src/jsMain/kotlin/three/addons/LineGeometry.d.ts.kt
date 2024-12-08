package three.addons

import three.Line

open external class LineGeometry : LineSegmentsGeometry {
    open val isLineGeometry: Boolean
    open fun fromLine(line: Line<*, *>): LineGeometry /* this */
}