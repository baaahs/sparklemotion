package baaahs.show

interface PatchHolder {
    val title: String
    val patches: List<Patch>
    val eventBindings: List<EventBinding>
    val controlLayout: Map<String, List<String>>
}