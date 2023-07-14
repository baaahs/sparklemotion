package baaahs.app.ui

import js.core.jso
import mui.material.PaletteMode
import mui.material.styles.Theme
import mui.material.styles.ThemeOptions
import mui.material.styles.createTheme

object Themes {
    private val BaseTheme = baseTheme {
        components = jso {
            MuiListSubheader = jso {
                styleOverrides = jso {
                    root = jso {
                        backgroundColor = "inherit"
                        lineHeight = "inherit"
                    }
                }
            }
            MuiFormControlLabel = jso {
                styleOverrides = jso {
                    root = jso {
                        userSelect = "none"
                    }
                }
            }
        }
        typography = jso {
            button = jso {
                textTransform = "none"
            }
        }
    }

    val Light = createTheme {
        palette = jso { mode = PaletteMode.light }
    }

    val Dark = createTheme {
        palette = jso { mode = PaletteMode.dark }
    }

    private fun baseTheme(block: ThemeOptions.() -> Unit): ThemeOptions =
        jso { block() }

    private fun createTheme(block: ThemeOptions.() -> Unit): Theme =
        createTheme(jso { apply(block) }, BaseTheme)
}