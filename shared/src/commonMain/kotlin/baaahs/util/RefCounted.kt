package baaahs.util

interface RefCounted {
    fun inUse(): Boolean
    fun use()
    fun disuse()
    fun onRelease()
}

class RefCounter : RefCounted {
    var refCount: Int = 0

    override fun inUse(): Boolean = refCount != 0

    override fun use() {
        refCount++
    }

    override fun disuse() {
        refCount--

        if (refCount < 0) error("Too many calls to disuse().")

        if (!inUse()) onRelease()
    }

    override fun onRelease() {
    }
}