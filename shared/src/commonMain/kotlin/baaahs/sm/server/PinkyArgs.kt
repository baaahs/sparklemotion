package baaahs.sm.server

import baaahs.libraries.ShaderLibraryManager
import org.koin.core.scope.Scope

interface PinkyArgs {
    // TODO: Use this.
    val sceneName: String?

    // TODO: Use this.
    val showName: String?

    // TODO: Use this.
    val switchShowAfter: Int?

    // TODO: Use this.
    val adjustShowAfter: Int?

    val simulateBrains: Boolean

    val subcommand: Subcommand?

    interface Subcommand {
        val name: String

        suspend fun Scope.execute()
    }

    companion object {
        val defaults: PinkyArgs = object : PinkyArgs {
            override val sceneName: String? = null
            override val showName: String? = null
            override val switchShowAfter: Int? = null
            override val adjustShowAfter: Int? = null
            override val simulateBrains: Boolean = false
            override val subcommand: Subcommand? = null
        }
    }
}