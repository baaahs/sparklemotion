package three.addons

import three.Mesh
import three.MeshBasicMaterial
import three.SphereGeometry
import three.Texture

open external class GroundedSkybox(map: Texture, height: Number, radius: Number, resolution: Number = definedExternally) : Mesh<SphereGeometry, MeshBasicMaterial>