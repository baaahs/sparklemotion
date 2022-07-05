package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.show.mutable.MutablePort
import baaahs.ui.Icon

interface LinkOption {
    val title: String
    val icon: Icon
    val groupName: String?
    val isAdvanced: Boolean get() = false

    fun getMutablePort(): MutablePort?
    fun matches(otherPort: MutablePort?): Boolean = otherPort == getMutablePort()
}

class PortLinkOption(
    private val mutablePort: MutablePort,
    val isPluginRef: Boolean = false,
    val wasPurposeBuilt: Boolean = false,
    val isStream: Boolean = false,
    val isLocalShaderOut: Boolean = false,
    val isPluginSuggestion: Boolean = false,
    val isExactContentType: Boolean = false,
    val isDefaultBinding: Boolean = false,
    val createsFilter: Boolean = false
): LinkOption {
    override val isAdvanced: Boolean
        get() = !isExactContentType

    override val title get() = mutablePort.title
    override val icon get() = mutablePort.icon
    override val groupName get() = mutablePort.groupName

    override fun getMutablePort(): MutablePort = mutablePort

    val priority: Int get() =
        pack(
            isPluginRef,
            createsFilter && isExactContentType,
            wasPurposeBuilt && isExactContentType,
            isStream,
            isLocalShaderOut,
            isPluginSuggestion,
            isExactContentType,
            wasPurposeBuilt,
            createsFilter,
            isDefaultBinding
        )

    private fun pack(vararg bits: Boolean): Int {
        var int = 0
        bits.forEach { bool ->
            int = int shl 1
            if (bool) int = int or 1
        }
        return int
    }

    override fun toString(): String {
        return "PortLinkOption(mutablePort=${mutablePort.groupName} ${mutablePort.title} priority=$priority)"
    }
}


object NoSourcePortOption : LinkOption {
    override val title: String get() = "Nothing"
    override fun getMutablePort(): MutablePort = error("not implemented")
    override val groupName: String? get() = null
    override val icon: Icon get() = CommonIcons.None
    override fun matches(otherPort: MutablePort?): Boolean = otherPort == null
//    override fun isAppropriateFor(inputPort: InputPort): Boolean = true
}

object NewSourcePortOption : LinkOption {
    override val title: String get() = "Create New…"
    override fun getMutablePort(): MutablePort = error("not implemented")
    override val groupName: String? get() = null
    override val icon: Icon get() = CommonIcons.Add
    override fun matches(otherPort: MutablePort?): Boolean = false
//    override fun isAppropriateFor(inputPort: InputPort): Boolean = true
}
