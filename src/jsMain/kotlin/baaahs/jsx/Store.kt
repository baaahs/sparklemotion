@file:JsModule("js/store.jsx")
@file:JsNonModule

package baaahs.jsx

import baaahs.SheepSimulator
import react.RContext

external interface Store {
    val state: StoreState
}

external interface StoreState {
    val sheepSimulator: SheepSimulator?
    val selectedShow: String
    val isConnected: Boolean
}

@JsName("store")
external val store: RContext<Store>