@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Mesh
import three.MeshBasicMaterial
import three.SphereGeometry
import three.Texture

open external class GroundedSkybox(map: Texture, height: Number, radius: Number, resolution: Number = definedExternally) : Mesh<SphereGeometry, MeshBasicMaterial>