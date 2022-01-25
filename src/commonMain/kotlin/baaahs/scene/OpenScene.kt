package baaahs.scene

import baaahs.model.Model
import baaahs.sm.webapi.Problem

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

    val allProblems: List<Problem>
        get() = buildList {
            model.visit { entity -> addAll(entity.problems) }
        }
}