package baaahs.app.ui.dev

import baaahs.app.ui.*
import baaahs.gl.Toolchain
import baaahs.ui.xComponent
import js.core.jso
import mui.material.CssBaseline
import mui.material.styles.ThemeProvider
import react.PropsWithChildren
import react.RBuilder
import react.RHandler

private val DevAppWrapperView = xComponent<DevAppWrapperProps>("DevAppWrapper") { props ->
    val myAppGlContext = memo { jso<AppGlContext> { this.sharedGlContext = null } }

    appContext.Provider {
        attrs.value = props.appContext

        toolchainContext.Provider {
            attrs.value = props.toolchain

            appGlContext.Provider {
                attrs.value = myAppGlContext

                ThemeProvider {
                    attrs.theme = props.appContext.allStyles.theme
                    CssBaseline {}

                    child(props.children!!)
                }
            }
        }
    }
}

external interface DevAppWrapperProps : PropsWithChildren {
    var appContext: AppContext
    var toolchain: Toolchain
}

fun RBuilder.devAppWrapper(handler: RHandler<DevAppWrapperProps>) =
    child(DevAppWrapperView, handler = handler)