package baaahs.controller

interface ControllerListener {
    fun onAdd(controller: Controller)
    fun onRemove(controller: Controller)
    fun onError(controller: Controller)
}