package baaahs.app.ui

import baaahs.app.ui.controls.Styles as AppUiControlsStyles
import external.markdownit.MarkdownIt
import external.shepherd.StepOptionsButton
import js.objects.jso
import baaahs.ui.unaryPlus
import external.shepherd.Position
import external.shepherd.StepOptions
import external.shepherd.loadCss
import kotlinx.css.RuleSet

class AppTour(
    private val allStyles: AllStyles
) {
    val tour = external.shepherd.Tour()

    init {
        loadCss()

        tour.apply {

            addStep(jso {
                title = "Ahoy-hoy!"
                text = MarkdownIt().render(
                    /** language=markdown */
                    """
                        Haaaay welcome to Sparkle Motion!
                        
                        Check it out, we've got you set up with a sample show.
                    """.trimIndent()
                )
                // attachTo = middle of the screen.
//            attachTo = jso {
//                element = ".${+themeStyles.appRoot}"
//                on = "center"
//            }
                buttons = arrayOf(button("Next"))
            })

            addStep(jso {
                text = MarkdownIt().render(
                    """
                        See all the nifty patterns and stuff down there? Try clicking on one you like.
                """.trimIndent()
                )
                arrow = true
                attachTo(allStyles.appUi.appContent, Position.topEnd)
                buttons = arrayOf(button("Next"))
            })

            addStep(jso {
                text = MarkdownIt().render(
                    """
                        Up here is a preview of your light show. Right now there's
                        a demo scene, but you can configure it if you'd like.
                    """.trimIndent()
                )
                arrow = true
                attachTo(AppUiControlsStyles.visualizerCard, Position.bottomStart)
                buttons = arrayOf(
                    jso {
                        text = "Yeah let's configure it!"
                        action = { tour.next() }
                    }
                )
            })

        }
    }

    fun button(
        text: String,
        action: () -> Unit = { tour.next() }
    ): StepOptionsButton {
        val nextButton = jso<StepOptionsButton> {
            this.text = text
            this.action = action
        }
        return nextButton
    }
}

fun StepOptions.attachTo(style: RuleSet, on: Position? = null) {
    attachTo = jso {
        element = ".${+style}"
        if (on != null) {
            this.on = on.name
        }
    }
}
