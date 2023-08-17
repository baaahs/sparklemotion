package baaahs.show.mutable

import baaahs.app.ui.editor.MutableEditable
import baaahs.show.Control
import baaahs.show.live.OpenControl

interface MutableControl : MutableEditable {
    var asBuiltId: String?

    val hasInternalLayout: Boolean
        get() = false

    fun buildControl(showBuilder: ShowBuilder): Control

    fun build(showBuilder: ShowBuilder) : Control =
        buildControl(showBuilder).also {
            asBuiltId = showBuilder.idFor(it)
        }

    fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        if (log.controls.add(this)) visitor.visit(this)
    }

    fun previewOpen(): OpenControl

    fun buildAndStashId(showBuilder: ShowBuilder): String {
        return showBuilder.idFor(buildControl(showBuilder))
            .also { asBuiltId = it }
    }
}