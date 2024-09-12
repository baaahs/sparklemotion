@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.Loader__1
import three.LoadingManager
import three.NormalOrGLBufferAttributes

external interface `T$84` {
    var atoms: Array<Array<Any>>
}

external interface PDB {
    var geometryAtoms: BufferGeometry<NormalOrGLBufferAttributes>
    var geometryBonds: BufferGeometry<NormalOrGLBufferAttributes>
    var json: `T$84`
}

open external class PDBLoader(manager: LoadingManager = definedExternally) : Loader__1<PDB> {
    open fun parse(text: String): PDB
}