package baaahs.app.ui

import baaahs.ui.child
import baaahs.ui.helper
import baaahs.ui.markdown
import baaahs.ui.title

object HelpText {
    val appToolbar = helper {
        title { +"What the..?" }

        child {
            markdown {
                +"""
                    Some help text about the screen.
                """.trimIndent()
            }
        }
    }
}

object PatchHolderEditorHelpText {
    val fixtures = helper {
        title { +"Fixtures" }
        child {
            markdown {
                +"""
                    **Fixtures** are Sparkle Motion output devices.
                    
                    > Right now Sparkle Motion only knows about **Pixel Arrays**, like the BAAAHS panels.
                    > In the future we'll support **Moving Heads**, and potentially other devices
                    > like flat screens, projectors, holodecks, etc.
                    
                    This column shows to which fixtures the patch on the right is applied.
                    
                    Currently we just support **All Surfaces**, so there will only be a single row here.
                """.trimIndent()
            }
        }

    }

    val patchOverview = helper {
        title { +"Patch Overview" }

        child {
            markdown {
                +"""
                    **Patches** are collections of one or more shaders, plus wiring information for
                    connecting the shaders with input sources and each other.
                """.trimIndent()
            }
        }
    }
}
