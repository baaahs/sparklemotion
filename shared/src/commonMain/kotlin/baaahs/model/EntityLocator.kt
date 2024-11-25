package baaahs.model

data class EntityLocator(val locator: String) {
    fun append(name: String) = EntityLocator("$locator:$name")

    companion object {
        private var nextId = 1

        fun next(): EntityLocator = EntityLocator(nextId++.toString())
    }
}