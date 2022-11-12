package baaahs.sm.server

import baaahs.libraries.ShaderLibraryManager
import kotlinx.cli.*
import org.koin.core.scope.Scope

@OptIn(ExperimentalCli::class)
class PinkyArgs(
    internal val parser: ArgParser
) {
    // TODO: Use this.
    val sceneName by parser.option(ArgType.String, shortName = "m")

    // TODO: Use this.
    val showName by parser.option(ArgType.String, "show", "s")

    // TODO: Use this.
    val switchShowAfter by parser.option(ArgType.Int, description = "Switch show after no input for x seconds")
        .default(600)

    // TODO: Use this.
    val adjustShowAfter by parser.option(
        ArgType.Int,
        description = "Start adjusting show inputs after no input for x seconds"
    )

    val simulateBrains by parser.option(ArgType.Boolean, description = "Simulate connected brains")
        .default(false)

    var subcommand: Subcommand? = null
        private set

    init {
        parser.subcommands(IndexShaderLibrary())
    }

    inner class IndexShaderLibrary : kotlinx.cli.Subcommand(
        "index-shader-library",
        "Generate an index for a shader library."
    ), Subcommand {
        val libraryName by argument(ArgType.String)

        override fun execute() { subcommand = this }

        override suspend fun Scope.execute() {
            get<ShaderLibraryManager>().buildIndex(libraryName)
        }
    }

    interface Subcommand {
        val name: String

        suspend fun Scope.execute()
    }

    companion object {
        val defaults: PinkyArgs = PinkyArgs(ArgParser("void"))
    }
}