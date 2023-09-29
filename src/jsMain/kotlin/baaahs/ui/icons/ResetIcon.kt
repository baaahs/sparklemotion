package baaahs.ui.icons

import mui.material.SvgIcon
import mui.material.SvgIconProps
import react.dom.svg.ReactSVG.path

val ResetIcon = react.FC<SvgIconProps> { props ->
    SvgIcon {
        path {
            d = "M13.26 3A9.003 9.003 0 0 0 4 12H2.21c-.45 0-.67.54-.35.85l2.79 2.79c.2.2.51.2.71 0l2.8-2.79c.3-.31.08-.85-.37-.85H6c0-3.89 3.2-7.06 7.1-7 3.71.05 6.84 3.18 6.9 6.9.06 3.91-3.1 7.1-7 7.1-1.59 0-3.05-.53-4.23-1.43-.4-.3-.96-.27-1.31.09-.43.43-.39 1.14.09 1.5A8.971 8.971 0 0 0 13 21c5.06 0 9.14-4.17 9-9.25-.13-4.7-4.05-8.62-8.74-8.75z"
        }
    }
}