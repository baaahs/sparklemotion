package baaahs.show.mutable

import baaahs.app.ui.editor.MutableEditable

interface MutableDocument<T> : MutableEditable {
    fun build(): T

    fun isChanged(originalDocument: T): Boolean =
        originalDocument != build()
}