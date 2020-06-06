package baaahs.show

import baaahs.glshaders.Patch

class ShowFile(
    val show: Show,
    val surfaces: List<Surfaces>,
    val shaders: List<Shader>
) {
    class Show(
        val name: String,
        val scenes: List<Scene>,
        val patchSets: List<PatchSet>,
        val eventBindings: List<EventBinding>
    )

    class Scene(
        val name: String,
        val patchSets: List<PatchSet>,
        val eventBindings: List<EventBinding>
    )

    class PatchSet(
        val name: String,
        val patchMappings: List<PatchMapping>,
        val eventBindings: List<EventBinding>
    )

    class PatchMapping(
        val patch: Patch,
        val surfaces: Surfaces
    )

    class EventBinding(
        val inputType: String,
        val inputData: Map<String, Any>,
        val targetType: String,
        val targetData: Map<String, Any>
    )

    class Surfaces(
        val name: String
    )

    class Shader {

    }

    companion object {
//        fun from(show: Show): ShowFile {
//            return ShowFile()
//        }
    }
}