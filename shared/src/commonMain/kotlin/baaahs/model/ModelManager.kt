package baaahs.model

import baaahs.ui.IObservable
import baaahs.ui.Observable

interface ModelManager : IObservable {
    fun findEntity(name: String): Model.Entity?
}

class ModelManagerImpl : Observable(), ModelManager {
    override fun findEntity(name: String): Model.Entity? {
        TODO("not implemented")
    }
}