@file:JsModule("js/simulator/store.jsx")
@file:JsNonModule

package baaahs.jsx.sim

import baaahs.SheepSimulator
import react.RContext

external interface Store {
    val state: StoreState
}

external interface StoreState {
    val simulator: SheepSimulator
    val selectedShow: String
    val isConnected: Boolean
}

@JsName("store")
external val store: RContext<Store>