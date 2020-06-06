package baaahs.show

import baaahs.glshaders.Patch

data class Show(
    val name: String,
    val scenes: List<Scene>,
    val patchSets: List<PatchSet>,
    val eventBindings: List<EventBinding>,
    val controls: List<Control>,
    val layouts: Layouts,
    val controlLayout: Map<String, List<Control>>
)

data class Scene(
    val name: String,
    val patchSets: List<PatchSet>,
    val eventBindings: List<EventBinding>,
    val controlLayout: Map<String, List<Control>>
)

data class PatchSet(
    val name: String,
    val patchMappings: List<PatchMapping>,
    val eventBindings: List<EventBinding>,
    val controlLayout: Map<String, List<Control>>
)

data class PatchMapping(
    val patch: Patch,
    val surfaces: Surfaces
)

data class EventBinding(
    val inputType: String,
    val inputData: Map<String, Any>,
    val target: Control
)

data class Control(
    val name: String,
    val type: String,
    val data: Map<String, Any>
)

data class Surfaces(
    val name: String
)

data class Layouts(
    val panelNames: List<String>,
    val map: Map<String, Layout>
)

data class Layout(
    val mosaicConfig: Any
)