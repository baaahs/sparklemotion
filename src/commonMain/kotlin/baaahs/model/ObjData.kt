package baaahs.model

import baaahs.geom.Vector3F

abstract class ObjModel(private val objData: ObjLoader.ObjData) : Model() {
    override val geomVertices: List<Vector3F> get() = objData.vertices
    override val allSurfaces: List<Surface> = objData.objs.map { obj -> createSurface(obj.name, obj.faces, obj.lines) }
    override val movingHeads: List<MovingHead>
        get() = emptyList()
    override val allEntities: List<Entity> get() = allSurfaces + movingHeads

    abstract fun createSurface(name: String, faces: List<Face>, lines: List<Line>): Surface
}