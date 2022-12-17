package baaahs.show.mutable

import baaahs.show.Surfaces

class VisitationLog {
    val patchHolders = mutableSetOf<MutablePatchHolder>()
    val patches = mutableSetOf<MutablePatch>()
    val surfaces = mutableSetOf<Surfaces>()
    val shaders = mutableSetOf<MutableShader>()
    val streams = mutableSetOf<MutableStream>()
    val controls = mutableSetOf<MutableControl>()
    val dataSources = mutableSetOf<MutableFeedPort>()
}