package baaahs.ui

import react.Props
import react.RComponent
import react.State
import web.timers.setTimeout
import kotlin.js.Promise

abstract class BComponent<P : Props, S : State>(props: P) : RComponent<P, S>(props), Observer {
    private var valid = true;

    open fun observing(props: P, state: S): List<Observable?> = emptyList()

    override fun componentDidMount() {
        observing(props, state).forEach { it?.addObserver(this) }
    }

    override fun componentWillUnmount() {
        observing(props, state).forEach { it?.removeObserver(this) }
        pendingUpdates.remove(this)
    }

    override fun componentDidUpdate(prevProps: P, prevState: S, snapshot: Any) {
        observing(prevProps, prevState).forEach { it?.removeObserver(this) }
        observing(props, state).forEach { it?.addObserver(this) }
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
                setTimeout({ performUpdate() }, 50)
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