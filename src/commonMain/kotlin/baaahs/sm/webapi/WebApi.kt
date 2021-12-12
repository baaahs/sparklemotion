package baaahs.sm.webapi

import baaahs.app.ui.CommonIcons
import baaahs.io.Fs
import baaahs.libraries.ShaderLibrary
import baaahs.randomId
import baaahs.ui.Icon
import kotlinx.serialization.Serializable

@Serializable
data class ClientData(
    val fsRoot: Fs.File
)

@Serializable
class NewCommand<T>(val template: T? = null)

@Serializable
class SwitchToCommand(val file: Fs.File?)

@Serializable
class SaveCommand

@Serializable
class SaveAsCommand(val file: Fs.File)

@Serializable
class SearchShaderLibraries(val terms: String) {
    @Serializable
    class Response(val matches: List<ShaderLibrary.Entry>)
}

@Serializable
data class ShowProblem(
    val title: String,
    val message: String? = null,
    val severity: Severity = Severity.ERROR,
    val id: String = randomId("error")
)

fun Collection<ShowProblem>.severity() = maxOfOrNull { it.severity }

enum class Severity(val icon: Icon) {
    INFO(CommonIcons.Info),
    WARN(CommonIcons.Warning),
    ERROR(CommonIcons.Error)
}