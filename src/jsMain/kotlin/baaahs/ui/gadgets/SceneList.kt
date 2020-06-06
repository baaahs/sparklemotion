package baaahs.ui.gadgets

import baaahs.PubSub
import baaahs.show.Scene
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.buttonGroup
import react.*

val SceneList = functionalComponent<SceneListProps> { props ->
    buttonGroup {
        attrs.variant = ButtonVariant.outlined
        props.scenes.forEach { scene ->
            button {
                +scene.name
                attrs.disabled = scene == props.currentScene
                attrs.onClickFunction = { props.onSelect(scene) }
            }
        }
    }
}

external interface SceneListProps: RProps {
    var pubSub: PubSub.Client
    var scenes: List<Scene>
    var currentScene: Scene
    var onSelect: (Scene) -> Unit
}

fun RBuilder.sceneList(handler: SceneListProps.() -> Unit): ReactElement =
    child(SceneList) { attrs { handler() } }
