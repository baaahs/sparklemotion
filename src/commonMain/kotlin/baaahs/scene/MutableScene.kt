package baaahs.scene

import baaahs.model.EntityData
import baaahs.model.ModelData

class MutableScene(
    baseScene: Scene
) {
    var title = baseScene.title
    val model = MutableModel(baseScene.model)
    val controllers = baseScene.controllers
        .mapValues { (_, v) -> MutableControllerConfig(v) }.toMutableMap()
    val fixtures = baseScene.fixtures
        .mapValues { (_, v) -> MutableFixture(v) }.toMutableMap()
}

class MutableControllerConfig(var controllerConfig: ControllerConfig)

class MutableFixture(var fixture: FixtureConfigNew)

class MutableModel(baseModel: ModelData) {
    var title = baseModel.title
    val entities = baseModel.entities.map { MutableEntity(it) }.toMutableList()
    var units = baseModel.units

//    fun edit(entity: Model.Entity) {
//        entities.forEach { candidate ->
//            if (candidate == entity) {
//                MutableEntity(candidate)
//            } else if (candidate is Model.EntityGroup) {
//
//            }
//        }
//    }
}

class MutableEntity(var entity: EntityData)
