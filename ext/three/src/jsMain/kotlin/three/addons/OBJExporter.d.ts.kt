package three.addons

import three.Object3D

open external class OBJExporter {
    open fun parse(obj: Object3D): String
}