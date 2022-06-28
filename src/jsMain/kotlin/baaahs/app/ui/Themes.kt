package baaahs.app.ui

import kotlinx.js.jso
import mui.material.PaletteMode
import mui.material.styles.createTheme

object Themes {
    val Light = createTheme(
        jso {
            palette = jso { mode = PaletteMode.light }
        }
    )

    val Dark = createTheme(
        jso {
            palette = jso { mode = PaletteMode.dark }
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
    )
}