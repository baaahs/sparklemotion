@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import org.w3c.dom.HTMLElement
import three.Mesh
import three.MeshBasicMaterial
import three.PlaneGeometry

open external class HTMLMesh(dom: HTMLElement) : Mesh<PlaneGeometry, MeshBasicMaterial> {
    open fun dispose()
}