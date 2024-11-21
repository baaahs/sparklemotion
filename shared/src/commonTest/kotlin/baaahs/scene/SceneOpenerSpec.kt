package baaahs.scene

import baaahs.describe
import baaahs.entityDataForTest
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.geom.identity
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.EntityData
import baaahs.model.EntityId
import baaahs.model.ModelData
import baaahs.only
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class SceneOpenerSpec : DescribeSpec({
    describe<SceneOpener> {
        val entityDictionary by value { mapOf<EntityId, EntityData>() }
        val entityIds by value { listOf<EntityId>() }
        val model by value { ModelData("Test Model", entityIds) }
        val scene by value { Scene(model, entityDictionary) }
        val openScene by value { scene.open() }

        it("should open a scene with no entities") {
            openScene.model.entities.shouldBeEmpty()
        }

        context("with an entity") {
            val entityData1 by value { entityDataForTest("some entity", position = Vector3F.unit3d) }
            override(entityDictionary) { mapOf("test-entity" to entityData1) }
            override(entityIds) { listOf("test-entity") }

            it("should open a scene with the entity") {
                val theEntity = openScene.model.entities.only()
                println("entityData1 = ${theEntity}")
                val expected = entityData1.open(Matrix4F.identity)

                theEntity.name shouldBe "some entity"
                theEntity.position shouldBe entityData1.position
                theEntity.rotation shouldBe entityData1.rotation
                theEntity.scale shouldBe entityData1.scale
                theEntity.transformation shouldBe Matrix4F.identity
                theEntity::class shouldBe expected::class
            }
        }
    }
})
