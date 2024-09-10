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

open external class AxesHelper(size: Number = definedExternally) : LineSegments__0 {
    override var override: Any
    override val type: String /* String | "AxesHelper" */
    open fun setColors(xAxisColor: Color, yAxisColor: Color, zAxisColor: Any /* Color | String | Number */): AxesHelper /* this */
    open fun setColors(xAxisColor: Color, yAxisColor: String, zAxisColor: Any /* Color | String | Number */): AxesHelper /* this */
    open fun setColors(xAxisColor: Color, yAxisColor: Number, zAxisColor: Any /* Color | String | Number */): AxesHelper /* this */
    open fun setColors(xAxisColor: String, yAxisColor: Color, zAxisColor: Any /* Color | String | Number */): AxesHelper /* this */
    open fun setColors(xAxisColor: String, yAxisColor: String, zAxisColor: Any /* Color | String | Number */): AxesHelper /* this */
    open fun setColors(xAxisColor: String, yAxisColor: Number, zAxisColor: Any /* Color | String | Number */): AxesHelper /* this */
    open fun setColors(xAxisColor: Number, yAxisColor: Color, zAxisColor: Any /* Color | String | Number */): AxesHelper /* this */
    open fun setColors(xAxisColor: Number, yAxisColor: String, zAxisColor: Any /* Color | String | Number */): AxesHelper /* this */
    open fun setColors(xAxisColor: Number, yAxisColor: Number, zAxisColor: Any /* Color | String | Number */): AxesHelper /* this */
    open fun dispose()
}