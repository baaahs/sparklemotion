package baaahs.scene

import baaahs.device.PixelArrayDevice
import baaahs.fixtures.FixtureMapping
import baaahs.geom.Matrix4F
import baaahs.geom.identity
import baaahs.getBang
import baaahs.model.EntityId
import baaahs.model.Model
import baaahs.model.ModelData

class SceneOpener(
    val scene: Scene,
) {
    val entityMap = hashMapOf<EntityId, Model.Entity>()

    fun buildEntity(id: EntityId, accumulatedMatrix: Matrix4F): Model.Entity {
        if (entityMap.containsKey(id))
            error("Entity \"$id\" already exists in the scene.")

        val entityData = scene.entities.getBang(id, "entity")
        val currentMatrix = accumulatedMatrix * entityData.transformation
        return entityData.open(currentMatrix)
            .also { entityMap[id] = it }
    }

    fun open(): OpenScene {
        return OpenScene(
            scene.model.open(),
            scene.controllers.mapValues { (controllerId, controllerConfig) ->
                OpenControllerConfig(
                    controllerId,
                    controllerConfig,
                    controllerConfig.fixtures.map { fixtureMappingData ->
                        fixtureMappingData.open()
                    })
            },
            isFallback = scene == Scene.Fallback
        )
    }

    fun ModelData.open(): Model =
        Model(
            title,
            entityIds.map { entityId ->
                buildEntity(entityId, Matrix4F.identity)
            },
            units,
            initialViewingAngle
        )

    fun FixtureMappingData.open(): FixtureMapping {
        val entity = entityId?.let { entityMap.getBang(it, "entity") }
        return FixtureMapping(
            entity,
            fixtureOptions
                ?: entity?.defaultFixtureOptions
                ?: PixelArrayDevice.defaultOptions,
            transportConfig
        )
    }
}