package baaahs.app.ui

import js.objects.jso
import mui.material.PaletteMode
import mui.material.styles.Theme
import mui.material.styles.ThemeOptions
import mui.material.styles.createTheme
import kotlin.js.JSON.parse

object Themes {
    private val BaseTheme = baseTheme {
        /**language=json*/
        components = parse("""
            {
                "MuiListSubheader": {
                    "styleOverrides": {
                        "root": {
                            "backgroundColor": "inherit",
                            "lineHeight": "inherit"
                        }
                    }
                },
                "MuiFormControlLabel": {
                    "styleOverrides": {
                        "root": {
                            "userSelect": "none"
                        }
                    }
                }
            }
        """.trimIndent())

        /**language=json*/
        typography = parse("""
            {
                "button": {
                    "textTransform": "none"
                }
            }
        """.trimIndent())
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