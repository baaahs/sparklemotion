@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface `T$84` {
    var atoms: Array<Array<Any>>
}

external interface PDB {
    var geometryAtoms: BufferGeometry__0
    var geometryBonds: BufferGeometry__0
    var json: `T$84`
}

external open class PDBLoader(manager: LoadingManager = definedExternally) : Loader__1<PDB> {
    open fun parse(text: String): PDB
}