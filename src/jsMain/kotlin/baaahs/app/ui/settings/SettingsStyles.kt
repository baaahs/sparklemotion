package baaahs.app.ui.settings

import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import styled.StyleSheet

object SettingsStyles : StyleSheet("app-ui-Settings", isStatic = true) {
    val tabsListCol by css {
        flex(1.0, 1.0, FlexBasis.zero)
    }
    val tabsList by css {
        important(::paddingLeft, 1.em)
    }
    val tabsListItemIcon by css {
        important(::minWidth, 1.em)
        paddingRight = 1.em
    }
    val tabsSubheader by css {
        important(::lineHeight, LineHeight.normal)
    }

}
