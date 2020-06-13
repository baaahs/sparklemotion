package baaahs.ui.gadgets

import baaahs.PubSub
import baaahs.show.Scene
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.buttonGroup
import materialui.components.card.card
import materialui.exclusive
import materialui.selected
import materialui.toggleButton
import materialui.toggleButtonGroup
import react.*

val SceneList = functionalComponent<SceneListProps> { props ->
    card {
        toggleButtonGroup {
            attrs.exclusive = true
            attrs.variant = ButtonVariant.outlined
            props.scenes.forEachIndexed { index, scene ->
                toggleButton {
                    +scene.title
//                (attrs as Tag).disabled = scene == props.currentScene
                    attrs.selected = index == props.selected
                    attrs.onClickFunction = { props.onSelect(index) }
                }
            }

            if (props.editMode) {
                button {
                    +"+"
//                attrs.onClickFunction = {  }
                }
            }
        }
    }
}

external interface SceneListProps: RProps {
    var pubSub: PubSub.Client
    var scenes: List<Scene>
    var selected: Int
    var onSelect: (Int) -> Unit
    var editMode: Boolean
}

fun RBuilder.sceneList(handler: SceneListProps.() -> Unit): ReactElement =
    child(SceneList) { attrs { handler() } }
