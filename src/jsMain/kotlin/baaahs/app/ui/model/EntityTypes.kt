package baaahs.app.ui.model

import baaahs.geom.Vector3F
import baaahs.model.*

interface EntityType {
    val title: String
    val addNewTitle: String get() = title

    fun createNew(): EntityData
}

val EntityTypes = listOf(
    GridEntityType, LightBarEntityType, LightRingEntityType, MovingHeadEntityType, ImportEntityType, HengeEntityType
)

object GridEntityType : EntityType {
    override val title: String = "Grid"

    override fun createNew(): EntityData = GridData(
        "New Grid",
        rows = 2, columns = 2, rowGap = 1f, columnGap = 1f,
        direction = GridData.Direction.RowsThenColumns
    )
}

object LightBarEntityType : EntityType {
    override val title: String = "Light Bar"

    override fun createNew(): EntityData = LightBarData(
        "New Light Bar",
        startVertex = Vector3F.origin,
        endVertex = Vector3F(1.0, 0.0, 0.0)
    )
}

object LightRingEntityType : EntityType {
    override val title: String = "Light Ring"

    override fun createNew(): EntityData = LightRingData("New Light Ring")
}

object MovingHeadEntityType : EntityType {
    override val title: String = "Moving Head"

    override fun createNew(): EntityData = MovingHeadData("New Moving Head", baseDmxChannel = 1)
}

//object SurfaceEntityType : EntityType {
//    override val title: String = "Surface"
//
//    override fun createNew(): EntityData = LightBarData("New Surface")
//}

object ImportEntityType : EntityType {
    override val title: String = "Imported Model"
    override val addNewTitle: String get() = "Import..."

    override fun createNew(): EntityData = ImportedEntityData("New OBJ", objData = "", objDataIsFileRef = false)
}

object HengeEntityType : EntityType {
    override val title: String = "Henge"

    override fun createNew(): EntityData = HengeData("New Henge")
}