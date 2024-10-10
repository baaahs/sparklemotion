package baaahs.sm.server

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

class PinkyArgs(parser: ArgParser) {
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

    companion object {
        val defaults: PinkyArgs = PinkyArgs(ArgParser("void"))
    }
}