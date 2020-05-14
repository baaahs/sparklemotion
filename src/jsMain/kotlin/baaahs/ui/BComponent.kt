package baaahs.ui

import react.RComponent
import react.RProps
import react.RState
import kotlin.browser.window
import kotlin.js.Promise

abstract class BComponent<P : RProps, S : RState>(props: P) : RComponent<P, S>(props), Observer {
    private var valid = true;

    open fun observing(props: P, state: S): List<Observable?> = emptyList()

    override fun componentDidMount() {
        observing(props, state).forEach { it?.addObserver(this) }
    }

    override fun componentWillUnmount() {
        observing(props, state).forEach { it?.removeObserver(this) }
    }

    override fun componentWillUpdate(nextProps: P, nextState: S) {
        observing(props, state).forEach { it?.removeObserver(this) }
        observing(nextProps, nextState).forEach { it?.addObserver(this) }
    }

    override fun notifyChanged() {
        if (valid) {
            valid = false
            scheduleUpdate(this)
        }
    }

    private fun performUpdate() {
        valid = true
        setState({ s -> s })
    }

    companion object {
        private val pendingUpdates = mutableListOf<BComponent<*, *>>()

        private fun scheduleUpdate(bComponent: BComponent<*, *>) {
            if (pendingUpdates.isEmpty()) {
                window.setTimeout({ performUpdate() }, 50)
            }
            pendingUpdates.add(bComponent)
        }

        private fun performUpdate() {
            val toUpdate = ArrayList(pendingUpdates)
            pendingUpdates.clear()

            // whatever tf this is...
            Promise.resolve("").then {
                unstable_batchedUpdates {
                    toUpdate.forEach { it.performUpdate() }
                }
            }
        }
    }
}