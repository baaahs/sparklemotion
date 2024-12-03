package baaahs.controller

import baaahs.ui.Observable
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
abstract class ControllerState : Observable() {
    abstract val title: String
    abstract val address: String?
    abstract val onlineSince: Instant?
    abstract val firmwareVersion: String?
    abstract val lastErrorMessage: String?
    abstract val lastErrorAt: Instant?

    open fun matches(controllerMatcher: ControllerMatcher): Boolean =
        controllerMatcher.matches(title, address)
}