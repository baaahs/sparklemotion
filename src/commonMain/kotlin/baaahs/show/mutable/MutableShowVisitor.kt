package baaahs.show.mutable

import baaahs.show.DataSource
import baaahs.show.Surfaces

interface MutableShowVisitor {
    fun visit(mutablePatchHolder: MutablePatchHolder) {}
    fun visit(mutablePatch: MutablePatch) {}
    fun visit(surfaces: Surfaces) {}
    fun visit(mutableControl: MutableControl) {}
    fun visit(mutableShader: MutableShader) {}
    fun visit(mutableStream: MutableStream) {}
    fun visit(dataSource: DataSource) {}
}