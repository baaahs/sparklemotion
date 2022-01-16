package baaahs.scene

import baaahs.model.Model

class OpenScene(
    val model: Model,
    val controllers: Map<String, ControllerConfig> = emptyMap(),
    val fixtures: Map<String, FixtureConfigNew> = emptyMap()
) {
    constructor(scene: Scene) : this(
        scene.model.open(),
        scene.controllers,
        scene.fixtures
    )
}