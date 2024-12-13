package baaahs.scene

import baaahs.app.ui.model.GridEntityType
import baaahs.controller.ControllerId
import baaahs.controller.SacnManager
import baaahs.describe
import baaahs.entityDataForTest
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.scene.mutable.SceneBuilder
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.types.shouldBeInstanceOf

class MutableSceneSpec : DescribeSpec({
    describe<MutableScene> {
        val mutableEntities by value<List<MutableEntity>> { emptyList() }
        val controllerConfigs by value<Map<ControllerId, MutableControllerConfig>> { emptyMap() }
        val fixtureMappings by value<Map<ControllerId, MutableList<MutableFixtureMapping>>> { emptyMap() }
        val mutableScene by value {
            MutableScene("test").apply {
                model.entities.addAll(mutableEntities)
                controllers.putAll(controllerConfigs)
                this.fixtureMappings.putAll(fixtureMappings)
            }
        }
        val sceneBuilder by value { SceneBuilder() }

        it("tries to keep entity names unique") {
            val firstGrid = GridEntityType.createNew().edit()
            val secondGrid = GridEntityType.createNew().edit()
            val thirdGrid = GridEntityType.createNew().edit()
            val fourthGrid = GridEntityType.createNew().edit().apply { title = "My Grid" }
            mutableScene.addEntity(firstGrid)
            mutableScene.addEntity(secondGrid)
            mutableScene.addEntity(thirdGrid)
            mutableScene.addEntity(fourthGrid)

            listOf(firstGrid, secondGrid, thirdGrid, fourthGrid).map { it.title }
                .shouldContainExactly("Grid", "Grid 2", "Grid 3", "My Grid")
        }

        context(".build") {
            it("builds an empty scene") {
                mutableScene.build()
                    .shouldBeInstanceOf<Scene>()
            }

            context("with entities and controllers and fixtures") {
                val panelA = entityDataForTest("panelA").edit()
                val panelB = entityDataForTest("panelB").edit()
                override(mutableEntities) { listOf(panelA, panelB) }

                val sacn1Config by value {
                    MutableSacnControllerConfig("SACN Controller", "192.168.1.150", 1, null, null)
                }
                val sacn1Id by value { ControllerId(SacnManager.controllerTypeName, sacn1Config.suggestId()) }
                val panelAMapping by value { MutableFixtureMapping(panelA, null, null) }
                val panelBMapping by value { MutableFixtureMapping(panelB, null, null) }

                override(fixtureMappings) {
                    mapOf(
                        sacn1Id to mutableListOf(panelAMapping, panelBMapping)
                    )
                }

                override(controllerConfigs) { mapOf(sacn1Id to sacn1Config) }

                it("builds a scene with an entities dictionary") {
                    mutableScene.build().entities
                        .shouldContainExactly(mapOf(
                            "panelA" to panelA.build(),
                            "panelB" to panelB.build()
                        ))
                }

                it("builds a scene model with entityId references") {
                    mutableScene.build().model.entityIds
                        .shouldContainExactly("panelA", "panelB")
                }

                it("builds a scene with controllers") {
                    mutableScene.build().controllers
                        .shouldContainExactly(mapOf(sacn1Id to sacn1Config.build(sceneBuilder)))
                }

                it("builds a scene with fixture mappings") {
                    mutableScene.build().fixtureMappings
                        .shouldContainExactly(mapOf(sacn1Id to listOf(
                            panelAMapping.build(sceneBuilder), panelBMapping.build(sceneBuilder))))
                }

                describe("#delete entity") {
                    it("removes entity") {
                        mutableScene.deleteEntity(panelA)

                        mutableScene.model.entities.shouldContainExactly(panelB)
                    }

                    it("removes fixture mappings too") {
                        mutableScene.deleteEntity(panelA)

                        mutableScene.fixtureMappings shouldContainExactly
                            mapOf(sacn1Id to listOf(panelBMapping))
                    }

                    it("removes controller entry entirely if all fixture mappings are removed") {
                        mutableScene.deleteEntity(panelA)
                        mutableScene.deleteEntity(panelB)

                        mutableScene.fixtureMappings.shouldBeEmpty()
                    }
                }
            }
        }
    }
})