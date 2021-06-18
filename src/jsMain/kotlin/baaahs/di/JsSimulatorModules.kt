package baaahs.di

import baaahs.FirmwareDaddy
import baaahs.MediaDevices
import baaahs.PermissiveFirmwareDaddy
import baaahs.browser.RealMediaDevices
import baaahs.dmx.Dmx
import baaahs.gl.GlBase
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.io.ResourcesFs
import baaahs.model.Model
import baaahs.net.Network
import baaahs.sim.*
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

class JsSimPlatformModule(
    network: FakeNetwork = FakeNetwork(),
    model: Model
) : JsPlatformModule(network, model) {
    override val Scope.mediaDevices: MediaDevices
        get() = FakeMediaDevices(get(), RealMediaDevices())
}

class JsSimulatorModule : SimulatorModule {
    override val Scope.fakeDmxUniverse: FakeDmxUniverse
        get() = FakeDmxUniverse()

    override fun getModule(): Module {
        return super.getModule().apply {
            single { Visualizer(get(), get()) }
            single<Fs>(named(Qualifier.MapperFs)) { FakeFs("Temporary Mapping Files") }
        }
    }

    enum class Qualifier {
        MapperFs
    }
}

class JsSimPinkyModule(
    private val pinkyLink_: Network.Link
) : PinkyModule {
    override val Scope.fs: Fs
        get() {
            val resourcesFs = ResourcesFs()
            val fs = MergedFs(
                BrowserSandboxFs("Browser Data"),
                get(named(JsSimulatorModule.Qualifier.MapperFs)),
                resourcesFs,
                name = "Browser Data"
            )
            return fs
        }

    override val Scope.firmwareDir: Fs.File
        get() = TODO("not implemented")

    override val Scope.firmwareDaddy: FirmwareDaddy
        get() = PermissiveFirmwareDaddy()
    override val Scope.pinkyMainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val Scope.pinkyLink: Network.Link
        get() = pinkyLink_
    override val Scope.dmxDriver: Dmx.Driver
        get() = SimDmxDriver()
    override val Scope.renderManager: RenderManager
        get() = RenderManager(get()) { GlBase.manager.createContext() }
}
