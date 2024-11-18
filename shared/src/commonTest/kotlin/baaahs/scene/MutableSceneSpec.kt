package baaahs.scene

import baaahs.controller.ControllerId
import baaahs.controller.SacnManager
import baaahs.describe
import baaahs.device.PixelArrayDevice
import baaahs.device.PixelFormat
import baaahs.dmx.MutableDmxTransportConfig
import baaahs.entityDataForTest
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.EntityData
import baaahs.scene.mutable.SceneBuilder
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.types.shouldBeInstanceOf

class MutableSceneSpec : DescribeSpec({
    describe<MutableScene> {
        val modelEntities by value<List<EntityData>> { emptyList() }
        val controllerConfigs by value<Map<ControllerId, MutableControllerConfig>> { emptyMap() }
        val mutableScene by value {
            MutableScene("test").apply {
                model.entities.addAll(modelEntities.map { it.edit() })
                controllers.putAll(controllerConfigs)
            }
        }

        context(".build") {
            it("builds an empty scene") {
                mutableScene.build()
                    .shouldBeInstanceOf<Scene>()
            }

            context("with entities and controllers") {
                val panelA = entityDataForTest("panelA")
                val panelB = entityDataForTest("panelB")
                override(modelEntities) { listOf(panelA, panelB) }

                val sacn1Fixtures by value { listOf<MutableFixtureMapping>(
                    MutableFixtureMapping(panelA.edit(),
                        PixelArrayDevice.MutableOptions(10, PixelFormat.RGB8, null, null),
                        MutableDmxTransportConfig(null, null, null)
                    )
                ) }
                val sacn1Config by value {
                    MutableSacnControllerConfig("SACN Controller", "192.168.1.150", 1, sacn1Fixtures.toMutableList(), null, null)
                }
                val sacn1Id by value { ControllerId(SacnManager.controllerTypeName, sacn1Config.suggestId()) }
                override(controllerConfigs) { mapOf(sacn1Id to sacn1Config) }

                it("builds a scene with an entities dictionary") {
                    mutableScene.build().entities
                        .shouldContainExactly(mapOf(
                            "panelA" to panelA,
                            "panelB" to panelB
                        ))
                }

                it("builds a scene model with entityId references") {
                    mutableScene.build().model.entityIds
                        .shouldContainExactly("panelA", "panelB")
                }

                it("builds a scene with controllers") {
                    mutableScene.build().controllers
                        .shouldContainExactly(mapOf(sacn1Id to sacn1Config.build(SceneBuilder())))
                }
            }
        }
    }
})