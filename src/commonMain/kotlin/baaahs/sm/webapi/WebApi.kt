package baaahs.sm.webapi

import baaahs.app.ui.CommonIcons
import baaahs.io.Fs
import baaahs.libraries.ShaderLibrary
import baaahs.randomId
import baaahs.show.Show
import baaahs.ui.Icon
import kotlinx.serialization.Serializable

@Serializable
data class ClientData(
    val fsRoot: Fs.File
)

@Serializable
class NewShowCommand(val template: Show? = null)

@Serializable
class SwitchToShowCommand(val file: Fs.File?)

@Serializable
class SaveShowCommand

@Serializable
class SaveAsShowCommand(val file: Fs.File)

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