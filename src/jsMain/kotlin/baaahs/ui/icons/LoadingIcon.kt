package baaahs.ui.icons

import mui.material.SvgIcon
import mui.material.SvgIconProps
import react.dom.svg.ReactSVG.animate
import react.dom.svg.ReactSVG.g
import react.dom.svg.ReactSVG.rect

val LoadingIcon = react.FC<SvgIconProps> { props ->
    SvgIcon {
        viewBox = "0 0 100 100"
        preserveAspectRatio = "xMidYMid"

        val steps = 12
        (0 until steps).forEach { i ->
            g {
                transform = "rotate(${360 / steps * i} 50 50)"

                rect {
                    x = 48.5
                    y = 24.0
                    rx = 1.5
                    ry = 2.76
                    width = 3.0
                    height = 12.0
                    fill = "#93b2e9"

                    animate {
                        attributeName = "opacity"
                        values = "1;0"
                        keyTimes = "0;1"
                        dur = "1s"
                        begin = "${-1 + i.toFloat() / steps}s"
                        repeatCount = "indefinite".asDynamic()
                    }
                }
            }
        }
    }
}