package baaahs.app.ui.editor

fun List<LinkOption>?.stringify(): String {
    if (this == null) return "no options!"

    val lines = arrayListOf<String>()
    var groupName: String? = null
    sortedWith(
        compareBy<LinkOption> { it.groupName }.thenBy { it.title }
    ).forEach { linkOption ->
        if (linkOption.groupName != groupName) {
            groupName = linkOption.groupName
            groupName?.let { lines.add(it) }
        }
        val selected = if (linkOption == first()) "*" else "-"
        val advanced = if (linkOption.isAdvanced) " (advanced)" else ""
        lines.add("$selected ${linkOption.title}$advanced")
    }
    return lines.joinToString("\n")
}