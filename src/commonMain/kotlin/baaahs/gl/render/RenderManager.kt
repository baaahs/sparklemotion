package baaahs.gl.render

import baaahs.device.FixtureType
import baaahs.fixtures.Fixture
import baaahs.fixtures.RenderPlan
import baaahs.gl.GlBase
import baaahs.gl.GlContext
import baaahs.util.Logger

interface RenderEngineBuilder {
    fun build(fixture: Fixture): RenderManager
}

enum class RenderRegime {
    Components {
        override val useSharedRenderEngine: Boolean
            get() = true
        override val locationStrategy: LocationStrategy
            get() = LocationStrategy.Discrete
    },

    Raster {
        override val useSharedRenderEngine: Boolean
            get() = false
        override val locationStrategy: LocationStrategy
            get() = LocationStrategy.Continuous
    };

    abstract val useSharedRenderEngine: Boolean
    abstract val locationStrategy: LocationStrategy
}

class PinkyRenderEngineBuilder(
    private val pinkyGlContext: GlContext
) : RenderEngineBuilder {
    override fun build(fixture: Fixture): RenderManager {
        val fixtureType = fixture.fixtureType

        when (fixtureType.renderRegime) {
            RenderRegime.Components ->
                ComponentRenderEngine(
                    pinkyGlContext, fixtureType,
                    resultDeliveryStrategy = pinkyGlContext.pickResultDeliveryStrategy()
                )

            RenderRegime.Raster -> TODO()

        }
    }
}

class RenderManager(renderEngineBuilder: RenderEngineBuilder) {
    private val renderEngines = mutableListOf<FixtureRenderEngine>()
    private val sharedRenderEngines = mutableMapOf<FixtureType, FixtureRenderEngine>()
    private val allRenderEngines
        get() = renderEngines + sharedRenderEngines.values

    fun addFixture(fixture: Fixture): FixtureRenderTarget {
        val fixtureType = fixture.fixtureType
        fixtureType.buildRenderEngine(fixtureType)
            .addFixture(fixture)
        val renderEngine = if (fixtureType.renderRegime.useSharedRenderEngine) {
            sharedRenderEngines.getOrPut(fixture.fixtureType) { renderEngineBuilder.build(fixtureType) }
            
            sharedRenderEngines[fixture.fixtureType]
        } else {
            RasterRenderEngine()
                .also { renderEngines.add(it) }
                .addFixture(fixture)
        } else if (fixture is ProjectorFixture) {
            val monitor = fixture.monitor ?: error("No monitor for ${fixture.title}.")
            val mode = fixture.mode ?: error("No mode for ${fixture.title}.")
            val monitorGlContext = GlBase.manager.createContext(monitor, mode)
//            val projector = fixture.projector
//            val monitor = projector.monitorName?.let { monitorName ->
//                monitors.all.find { it.name == monitorName }
//                    ?: error("No monitor named ${projector.monitorName}.")
//            } ?: monitors.all.firstOrNull() ?: error("No monitors found.")
//
//            val mode = monitor.modes.find { it.width == projector.width && it.height == projector.height }
//                ?: monitor.modes.maxBy { it.width * it.height }
//                ?: error("No monitor modes.")
//
//            val monitorGlContext = GlBase.manager.createContext(monitor, mode)
            val renderEngine = RasterRenderEngine(monitorGlContext, mode.width, mode.height, fixture.fixtureType)
            renderEngines.add(renderEngine)
            renderEngine.addFixture(fixture)
        } else error("Unsupported fixture type ${fixture.fixtureType.title}.")
    }

    suspend fun draw() {
        // If there are multiple RenderEngines, let them parallelize the render step...
        renderEngines.forEach { it.draw() }

        // ... before transferring results back to CPU memory.
        renderEngines.forEach { it.finish() }
    }

    fun setRenderPlan(renderPlan: RenderPlan) {
        renderEngines.forEach { engine ->
            val fixtureType = engine.fixtureType
            val fixtureTypeRenderPlan = renderPlan.fixtureTypes[fixtureType]
            engine.setRenderPlan(fixtureTypeRenderPlan)
            if (fixtureTypeRenderPlan == null) {
                logger.debug { "No render plan for ${fixtureType.title}" }
            }
        }
    }

    fun logStatus() {
        renderEngines.forEach { it.logStatus() }
    }

    fun release() {
        renderEngines.forEach { it.release() }
        renderEngines.clear()
    }

    companion object {
        private val logger = Logger<RenderManager>()
    }
}