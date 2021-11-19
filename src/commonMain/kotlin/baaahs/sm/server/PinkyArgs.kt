package baaahs.sm.server

import baaahs.Pluggables
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

class PinkyArgs(parser: ArgParser) {
    val model by parser.option(ArgType.String, shortName = "m")
        .default(Pluggables.defaultModel)

    val showName by parser.option(ArgType.String, "show", "s")

    val switchShowAfter by parser.option(ArgType.Int, description = "Switch show after no input for x seconds")
        .default(600)

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