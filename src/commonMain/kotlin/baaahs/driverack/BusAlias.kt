package baaahs.driverack

import baaahs.getBang
import baaahs.ui.Observable

class BusAlias(bus: Bus) : Bus {
    var bus: Bus = bus
        set(toBus) {
            channels.values.forEach { channel -> channel.switchTo(toBus) }
            field = toBus
        }

    override val id: String
        get() = bus.id
    override val driveRack: DriveRack
        get() = bus.driveRack

    private val channels = driveRack.rackMap.entries.associate { entry ->
        entry.id to AliasChannel(entry, bus)
    }

    override fun <T> channel(id: String): Channel<T> = channels.getBang(id, "channel") as Channel<T>

    internal class AliasChannel<T>(
        internal val entry: RackMap.Entry<T>,
        private var bus: Bus
    ) : Channel<T>, Observable() {
        private var _value: T = entry.valueFrom(bus)

        override var value: T
            get() = _value
            set(value) {
                _value = value
                entry.setValue(bus, value)
            }

        fun switchTo(toBus: Bus) {
            bus = toBus
            val toValue = entry.valueFrom(toBus)
            if (_value != toValue) {
                _value = toValue
                notifyChanged()
            }
        }
    }
}