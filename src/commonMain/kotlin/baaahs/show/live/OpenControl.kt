package baaahs.show.live

import baaahs.Gadget
import baaahs.camelize
import baaahs.randomId
import baaahs.show.DataSource
import baaahs.show.mutable.MutableButtonGroupControl
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableGadgetControl

interface OpenControl {
    val id: String
    fun controlledDataSources(): Set<DataSource> = emptySet()
    fun edit(): MutableControl
}

class OpenGadgetControl(
    val gadget: Gadget,
    val controlledDataSource: DataSource
) : OpenControl {
    override val id: String = randomId(gadget.title.camelize() + "Control")

    override fun controlledDataSources(): Set<DataSource> = setOf(controlledDataSource)

    override fun edit(): MutableGadgetControl = MutableGadgetControl(gadget, controlledDataSource)
}

class OpenButtonGroupControl(val title: String) : OpenControl {
    override val id: String
        get() = title.camelize() + "ButtonGroupControl"

    override fun edit(): MutableControl = MutableButtonGroupControl(title)
}