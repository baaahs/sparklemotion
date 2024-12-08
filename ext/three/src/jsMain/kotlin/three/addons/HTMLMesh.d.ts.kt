package three.addons

import org.w3c.dom.HTMLElement
import three.Mesh
import three.MeshBasicMaterial
import three.PlaneGeometry

open external class HTMLMesh(dom: HTMLElement) : Mesh<PlaneGeometry, MeshBasicMaterial> {
    open fun dispose()
}