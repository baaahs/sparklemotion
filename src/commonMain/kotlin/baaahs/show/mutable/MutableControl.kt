package baaahs.show.mutable

import baaahs.app.ui.MutableEditable
import baaahs.show.Control

interface MutableControl : MutableEditable {
    var asBuiltId: String?
    fun build(showBuilder: ShowBuilder): Control
    fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        if (log.controls.add(this)) visitor.visit(this)
    }

    fun buildAndStashId(showBuilder: ShowBuilder): String {
        return showBuilder.idFor(build(showBuilder))
            .also { asBuiltId = it }
    }
}