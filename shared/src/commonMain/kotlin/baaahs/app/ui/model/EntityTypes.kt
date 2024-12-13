package baaahs.app.ui.model

import baaahs.geom.Vector3F
import baaahs.model.EntityData
import baaahs.model.GridData
import baaahs.model.ImportedEntityData
import baaahs.model.LightBarData
import baaahs.model.LightRingData
import baaahs.model.MovingHeadData

interface EntityType {
    val title: String
    val addNewTitle: String get() = title

    fun createNew(): EntityData
}

object GridEntityType : EntityType {
    override val title: String = "Grid"

    override fun createNew(): EntityData = GridData(
        title,
        rows = 2, columns = 2, rowGap = 1f, columnGap = 1f,
        direction = GridData.Direction.RowsThenColumns
    )
}

object LightBarEntityType : EntityType {
    override val title: String = "Light Bar"

    override fun createNew(): EntityData =
        LightBarData(
            title,
            startVertex = Vector3F.Companion.origin,
            endVertex = Vector3F(1.0, 0.0, 0.0)
        )
}

object LightRingEntityType : EntityType {
    override val title: String = "Light Ring"

    override fun createNew(): EntityData =
        LightRingData(title)
}

object MovingHeadEntityType : EntityType {
    override val title: String = "Moving Head"

    override fun createNew(): EntityData =
        MovingHeadData(title, baseDmxChannel = 1)
}

//object SurfaceEntityType : EntityType {
//    override val title: String = "Surface"
//
//    override fun createNew(): EntityData = LightBarData("New Surface")
//}

object ImportEntityType : EntityType {
    override val title: String = "Imported Model"
    override val addNewTitle: String get() = "Import..."

    override fun createNew(): EntityData =
        ImportedEntityData(title, objData = "", objDataIsFileRef = false)
}