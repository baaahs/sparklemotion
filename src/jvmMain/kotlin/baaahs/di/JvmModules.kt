package baaahs.di

import baaahs.MediaDevices
import baaahs.PinkySettings
import baaahs.dmx.Dmx
import baaahs.dmx.JvmFtdiDmxDriver
import baaahs.io.Fs
import baaahs.io.RealFs
import baaahs.midi.JvmMidiDevices
import baaahs.midi.MidiDevices
import baaahs.net.JvmNetwork
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.plugin.ServerPlugins
import baaahs.sm.brain.DirectoryDaddy
import baaahs.sm.brain.FirmwareDaddy
import baaahs.sm.brain.proto.Ports
import baaahs.util.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import java.io.File
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage

class JvmPlatformModule(
    private val clock_: Clock
) : PlatformModule {
    override val network: Network = JvmNetwork()
    override val Scope.clock: Clock
        get() = clock_
    override val Scope.mediaDevices: MediaDevices
        get() = object : MediaDevices {
            override suspend fun enumerate(): List<MediaDevices.Device> = emptyList()
            override fun getCamera(selectedDevice: MediaDevices.Device?): MediaDevices.Camera =
                TODO("not implemented")
        }
}

class JvmPinkyModule(
    private val programName: String,
    private val startupArgs: Array<String>
) : PinkyModule {
    private val dataDir = File(System.getProperty("user.home")).toPath().resolve("sparklemotion/data")

    init {
        val name = "iCON iControls V2.04 Port 1"
        val transmitters = MidiSystem.getMidiDeviceInfo().mapNotNull { info ->
            println("${info.name}: ${info.javaClass.simpleName}\n  DESC=${info.description}\n  VENDOR=${info.vendor}\n  VERSION=${info.version}")
            val device = MidiSystem.getMidiDevice(info)
            val maxTransmitters = device.maxTransmitters
            if (maxTransmitters == -1 || maxTransmitters > 0) {
                device
            } else null
        }
        val transmitterDevice = transmitters.firstOrNull { it.deviceInfo.description == name }
        transmitterDevice?.let {
            it.open()
            val transmitter = it.transmitter
            transmitter.receiver = object : Receiver {
                override fun close() {
                    println("close!")
                }

                override fun send(message: MidiMessage?, timeStamp: Long) {
                    when (message) {
                        is ShortMessage -> println("MIDI: " +
                                "channel=${message.channel} command=${message.command} " +
                                "data1=${message.data1} data2=${message.data2}")
                        else -> println("send! $message $timeStamp")
                    }
                }
            }
        }
    }

    override val Scope.serverPlugins: ServerPlugins
        get() = Plugins.buildForServer(get(), get(named(PluginsModule.Qualifier.ActivePlugins)), programName, startupArgs)
    override val Scope.fs: Fs
        get() = RealFs("Sparkle Motion Data", dataDir)
    override val Scope.firmwareDir: Fs.File
        get() = RealFs(
            "Sparkle Motion Firmware",
            File(System.getProperty("user.home")).toPath()
        ).resolve("sparklemotion/fw")

    override val Scope.firmwareDaddy: FirmwareDaddy
        get() {
            val fwDir = get<Fs.File>(named("firmwareDir"))
            val link = get<Network.Link>()
            val myAddress = link.myAddress as JvmNetwork.IpAddress
            val fwUrlBase = "http://${myAddress.address.hostAddress}:${Ports.PINKY_UI_TCP}/fw"
            return DirectoryDaddy(fwDir, fwUrlBase)
        }

    override val Scope.pinkyMainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Default.limitedParallelism(1)
    override val Scope.dmxDriver: Dmx.Driver
        get() = JvmFtdiDmxDriver
    override val Scope.midiDevices: MidiDevices
        get() = JvmMidiDevices()
    override val Scope.pinkySettings: PinkySettings
        get() = PinkySettings()
}
